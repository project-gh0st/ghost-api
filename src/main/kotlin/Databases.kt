package net.raquezha.ghost

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Application.configureDatabases(
    jdbcUrl: String? = null,
    driver: String? = null,
    user: String? = null,
    password: String? = null
) {
    val env = EnvHelper(EnvHelper.REQUIRED_DB_VARS)
    val database = Database.connect(
        url = jdbcUrl ?: "jdbc:mysql://${env.get(EnvHelper.DB_HOST)}:${env.get(EnvHelper.DB_PORT)}/${env.get(EnvHelper.DB_NAME)}?useSSL=false&serverTimezone=UTC",
        driver = driver ?: "com.mysql.cj.jdbc.Driver",
        user = user ?: env.get(EnvHelper.DB_USER),
        password = password ?: env.get(EnvHelper.DB_PASSWORD)
    )

    transaction(database) {
        SchemaUtils.create(Meetings, UserService.Users)
    }

    val userService = UserService(database)
    routing {
        // Create user
        post("/users") {
            val user = call.receive<ExposedUser>()
            val id = userService.create(user)
            call.respond(HttpStatusCode.Created, id)
        }

        // Read user
        get("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = userService.read(id)
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update user
        put("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = call.receive<ExposedUser>()
            userService.update(id, user)
            call.respond(HttpStatusCode.OK)
        }

        // Delete user
        delete("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            userService.delete(id)
            call.respond(HttpStatusCode.OK)
        }

        // Health check endpoint
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        // Create meeting
        post("/meetings") {
            try {
                val req = call.receive<MeetingRequest>()
                val formatter = DateTimeFormatter.ISO_DATE_TIME
                val start = LocalDateTime.parse(req.startTime, formatter)
                val end = LocalDateTime.parse(req.endTime, formatter)
                val meetingId = transaction(database) {
                    Meetings.insert {
                        it[title] = req.title
                        it[description] = req.description
                        it[startTime] = start
                        it[endTime] = end
                    } get Meetings.id
                }
                println("Inserted meetingId: $meetingId")
                val meetingResult = transaction(database) {
                    Meetings.selectAll().filter { it[Meetings.id] == meetingId }
                }
                println("Rows found for meetingId $meetingId: ${meetingResult.size}")
                when {
                    meetingResult.isEmpty() -> {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Meeting not found after insert."))
                    }
                    meetingResult.size > 1 -> {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Multiple meetings found for inserted ID."))
                    }
                    else -> {
                        val meeting = meetingResult.first().toMeetingResponse()
                        call.respond(HttpStatusCode.Created, meeting)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // List all meetings
        get("/meetings") {
            val meetings = transaction(database) {
                Meetings.selectAll().map { it.toMeetingResponse() }
            }
            call.respond(meetings)
        }
    }
}

fun ResultRow.toMeetingResponse(): MeetingResponse {
    return MeetingResponse(
        id = this[Meetings.id],
        title = this[Meetings.title],
        description = this[Meetings.description],
        startTime = this[Meetings.startTime].toString(),
        endTime = this[Meetings.endTime].toString()
    )
}
