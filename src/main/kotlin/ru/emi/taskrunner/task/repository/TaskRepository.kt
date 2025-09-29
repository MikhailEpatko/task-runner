package ru.emi.taskrunner.task.repository

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.stereotype.Repository
import ru.emi.taskrunner.common.enums.TaskActivityStatus
import ru.emi.taskrunner.common.enums.TaskExecutionStatus
import ru.emi.taskrunner.common.repository.CoroutineSortingCrudRepository
import ru.emi.taskrunner.task.model.TaskEntity

@Repository
interface TaskRepository : CoroutineSortingCrudRepository<TaskEntity, Long> {
    suspend fun findByEnabledTrue(): List<TaskEntity>

    @Modifying
    @Query("update task set activity_status = :activityStatus where id != 0")
    suspend fun resetActivityStatusForAll(activityStatus: TaskActivityStatus)

    @Modifying
    @Query(
        """update task
           set activity_status = :activityStatus,
               last_execution_status = :lastExecutionStatus
           where id = :id""",
    )
    suspend fun updateStatuses(
        id: Long,
        activityStatus: TaskActivityStatus,
        lastExecutionStatus: TaskExecutionStatus,
    )
}