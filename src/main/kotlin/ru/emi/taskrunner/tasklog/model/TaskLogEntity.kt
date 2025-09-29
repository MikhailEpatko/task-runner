package ru.emi.taskrunner.tasklog.model

import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.emi.taskrunner.common.enums.TaskExecutionStatus
import java.time.Instant
import java.time.temporal.ChronoUnit

@Table("task_log")
data class TaskLogEntity(
    @Id
    val id: Long? = null,
    @Column("task_id")
    val taskId: Long,
    @Column("execution_status")
    @Enumerated(EnumType.STRING)
    val executionStatus: TaskExecutionStatus,
    @Column("error")
    val error: String? = null,
    @Column("started_at")
    val startedAt: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS),
    @Column("finished_at")
    val finishedAt: Instant? = null,
)