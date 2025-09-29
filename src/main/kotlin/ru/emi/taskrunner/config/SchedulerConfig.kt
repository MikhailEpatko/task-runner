package ru.emi.taskrunner.config

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTask
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.scheduling.config.TriggerTask
import org.springframework.scheduling.support.CronTrigger
import ru.emi.taskrunner.common.SchedulingTask
import ru.emi.taskrunner.common.enums.TaskActivityStatus
import ru.emi.taskrunner.scheduled.RunTask
import ru.emi.taskrunner.task.model.TaskEntity
import ru.emi.taskrunner.task.repository.TaskRepository
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Configuration
@EnableScheduling
class SchedulerConfig(
    private val tasks: TaskRepository,
    private val runTask: RunTask,
    private val nameToTaskRunnerMap: Map<String, SchedulingTask>,
) : SchedulingConfigurer {
    private val log = LoggerFactory.getLogger(javaClass.simpleName)
    private val scheduledTasks = ConcurrentHashMap<String, ScheduledTask>()
    private val exceptionHandler = CoroutineExceptionHandler { _, ex -> log.error(ex.stackTraceToString()) }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + exceptionHandler)

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        runBlocking { tasks.resetActivityStatusForAll(TaskActivityStatus.PENDING) }
        taskRegistrar.scheduler
            ?.scheduleAtFixedRate(
                { checkForUpdates(taskRegistrar) },
                Duration.ofMinutes(1L),
            )
    }

    private fun checkForUpdates(taskRegistrar: ScheduledTaskRegistrar) =
        runBlocking {
            tasks
                .findByEnabledTrue()
                .forEach { scheduleTask(taskRegistrar, it) }
        }

    private suspend fun scheduleTask(
        taskRegistrar: ScheduledTaskRegistrar,
        task: TaskEntity,
    ) {
        if (nameToTaskRunnerMap[task.name]?.isTaskInProgress?.get() ?: true) {
            return
        }
        val scheduledTask = scheduledTasks[task.name]
        scheduledTask?.cancel()
        val trigger = TriggerTask(
            { scope.launch { runTask.execute(task.id) } },
            { triggerContext -> CronTrigger(task.cron).nextExecution(triggerContext) },
        )
        taskRegistrar
            .scheduleTriggerTask(trigger)
            ?.let { scheduledTasks[task.name] = it }
    }
}