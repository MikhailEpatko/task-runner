package ru.emi.taskrunner.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import ru.emi.taskrunner.common.SchedulingTask

@Configuration
class BeanConfig {
    @Primary
    @Bean
    fun threadPoolTaskScheduler(
        @Value("\${spring.task.scheduling.pool.size}")
        poolSize: Int,
        @Value("\${spring.task.scheduling.virtual-threads}")
        virtualThreads: Boolean,
    ): ThreadPoolTaskScheduler =
        ThreadPoolTaskScheduler()
            .apply {
                setPoolSize(poolSize)
                setVirtualThreads(virtualThreads)
            }

    @Bean
    fun nameToTaskRunnerMap(tasks: List<SchedulingTask>): Map<String, SchedulingTask> = tasks.associateBy { it.name }
}