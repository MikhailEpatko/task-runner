package ru.emi.taskrunner.tasklog.repository

import org.springframework.stereotype.Repository
import ru.emi.taskrunner.common.repository.CoroutineSortingCrudRepository
import ru.emi.taskrunner.tasklog.model.TaskLogEntity

@Repository
interface TaskLogRepository : CoroutineSortingCrudRepository<TaskLogEntity, Long>