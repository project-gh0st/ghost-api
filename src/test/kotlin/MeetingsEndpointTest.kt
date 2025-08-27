package net.raquezha.ghost

import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MeetingsEndpointTest {
    @Test
    fun `should create and list meetings when valid meeting is posted`() = testApplication {
        application {
            configureDatabases(
                jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
                driver = "org.h2.Driver",
                user = "sa",
                password = ""
            )
            module()
        }
        // List meetings should be empty
        val emptyListResponse = client.get("/meetings")
        assertEquals(HttpStatusCode.OK, emptyListResponse.status)
        assertEquals("[]", emptyListResponse.bodyAsText())
        // Create meeting
        val createResponse = client.post("/meetings") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "title": "Team Sync",
                    "description": "Weekly team meeting",
                    "startTime": "2025-08-26T01:00",
                    "endTime": "2025-08-26T02:00"
                }
            """)
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        // List meetings should contain the new meeting
        val listResponse = client.get("/meetings")
        assertEquals(HttpStatusCode.OK, listResponse.status)
        val meetings = listResponse.bodyAsText()
        assert(meetings.contains("Team Sync"))
    }
}
