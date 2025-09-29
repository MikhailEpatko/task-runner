package ru.emi.taskrunner.task.model

import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.emi.taskrunner.common.enums.TaskActivityStatus
import ru.emi.taskrunner.common.enums.TaskExecutionStatus
import java.time.Instant

@Table("task")
data class TaskEntity(
    @Id
    val id: Long,
    @Column("name")
    val name: String,
    @Column("description")
    val description: String,
    @Column("cron")
    val cron: String,
    @Column("activity_status")
    @Enumerated(EnumType.STRING)
    val activityStatus: TaskActivityStatus,
    @Column("last_execution_status")
    @Enumerated(EnumType.STRING)
    val lastExecutionStatus: TaskExecutionStatus?,
    @Column("enabled")
    val enabled: Boolean,
    @Column("created_at")
    val createdAt: Instant,
    @Column("modified_at")
    val modifiedAt: Instant,
)