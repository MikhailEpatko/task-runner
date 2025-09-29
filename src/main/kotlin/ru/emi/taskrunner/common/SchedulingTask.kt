package ru.emi.taskrunner.common

import java.util.concurrent.atomic.AtomicBoolean

abstract class SchedulingTask {

    abstract val name: String

    val isTaskInProgress = AtomicBoolean(false)

    abstract suspend fun execute()
}