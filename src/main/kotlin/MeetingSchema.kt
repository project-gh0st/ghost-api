package net.raquezha.ghost

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Meetings : Table("meetings") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val description = text("description")
    val startTime = datetime("start_time")
    val endTime = datetime("end_time")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class MeetingRequest(
    val title: String,
    val description: String,
    val startTime: String, // ISO 8601 string
    val endTime: String    // ISO 8601 string
)

@Serializable
data class MeetingResponse(
    val id: Int,
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String
)
