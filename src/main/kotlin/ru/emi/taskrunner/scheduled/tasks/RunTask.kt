package ru.emi.taskrunner.scheduled.tasks

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.emi.taskrunner.common.SchedulingTask
import ru.emi.taskrunner.common.enums.TaskActivityStatus
import ru.emi.taskrunner.common.enums.TaskExecutionStatus
import ru.emi.taskrunner.task.repository.TaskRepository
import ru.emi.taskrunner.tasklog.model.TaskLogEntity
import ru.emi.taskrunner.tasklog.repository.TaskLogRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class RunTask(
    private val nameToTaskRunnerMap: Map<String, SchedulingTask>,
    private val tasks: TaskRepository,
    private val taskLogs: TaskLogRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass.simpleName)

    suspend fun execute(taskId: Long) {
        val task = tasks.findById(taskId)
        if (task == null || task.activityStatus == TaskActivityStatus.IN_PROGRESS || !task.enabled) {
            return
        }
        val taskRunner = nameToTaskRunnerMap[task.name]
        if (taskRunner == null) {
            log.error("Task runner not found for task '{}'", task.name)
            tasks.updateStatuses(
                id = task.id,
                activityStatus = TaskActivityStatus.PENDING,
                lastExecutionStatus = TaskExecutionStatus.FAILED,
            )
            taskLogs.save(
                TaskLogEntity(
                    taskId = taskId,
                    executionStatus = TaskExecutionStatus.FAILED,
                    error = "Task runner not found",
                    finishedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
                ),
            )
            return
        }
        if (taskRunner.isTaskInProgress.get()) {
            return
        }
        taskRunner.isTaskInProgress.set(true)
        tasks.updateStatuses(
            id = task.id,
            activityStatus = TaskActivityStatus.IN_PROGRESS,
            lastExecutionStatus = TaskExecutionStatus.STARTED,
        )
        val taskLog = taskLogs.save(
            TaskLogEntity(
                taskId = taskId,
                executionStatus = TaskExecutionStatus.STARTED,
            ),
        )
        try {
            taskRunner.execute()
            tasks.updateStatuses(
                id = task.id,
                activityStatus = TaskActivityStatus.PENDING,
                lastExecutionStatus = TaskExecutionStatus.SUCCESS,
            )
            taskLogs.save(
                taskLog.copy(
                    executionStatus = TaskExecutionStatus.SUCCESS,
                    finishedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
                ),
            )
        } catch (ex: Exception) {
            log.error("Task '{}' execution error: {}", task.name, ex.stackTraceToString())
            tasks.updateStatuses(
                id = task.id,
                activityStatus = TaskActivityStatus.PENDING,
                lastExecutionStatus = TaskExecutionStatus.FAILED,
            )
            taskLogs.save(
                taskLog.copy(
                    taskId = taskId,
                    executionStatus = TaskExecutionStatus.FAILED,
                    error = ex.message ?: ex.javaClass.simpleName,
                    finishedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
                ),
            )
        } finally {
            taskRunner.isTaskInProgress.set(false)
        }
    }
}