package net.raquezha.ghost

import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureDatabases(
                jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
                driver = "org.h2.Driver",
                user = "sa",
                password = ""
            )
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testHealthEndpoint() = testApplication {
        application {
            configureDatabases(
                jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
                driver = "org.h2.Driver",
                user = "sa",
                password = ""
            )
            module()
        }
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("{\"status\":\"ok\"}", response.bodyAsText())
    }

    @Test
    fun testCreateAndListMeetings() = testApplication {
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

    @Test
    fun testUserEndpoints() = testApplication {
        application {
            configureDatabases(
                jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
                driver = "org.h2.Driver",
                user = "sa",
                password = ""
            )
            module()
        }
        // Create user
        val createResponse = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "Alice",
                    "age": 30
                }
            """)
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        val userId = createResponse.bodyAsText().toIntOrNull()
        assert(userId != null && userId > 0)

        // Get user
        val getResponse = client.get("/users/$userId")
        assertEquals(HttpStatusCode.OK, getResponse.status)
        val userJson = getResponse.bodyAsText()
        assert(userJson.contains("Alice"))
        assert(userJson.contains("30"))

        // Update user
        val updateResponse = client.put("/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "Alice Updated",
                    "age": 31
                }
            """)
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)

        // Get updated user
        val getUpdatedResponse = client.get("/users/$userId")
        assertEquals(HttpStatusCode.OK, getUpdatedResponse.status)
        val updatedUserJson = getUpdatedResponse.bodyAsText()
        assert(updatedUserJson.contains("Alice Updated"))
        assert(updatedUserJson.contains("31"))

        // Delete user
        val deleteResponse = client.delete("/users/$userId")
        assertEquals(HttpStatusCode.OK, deleteResponse.status)

        // Get deleted user
        val getDeletedResponse = client.get("/users/$userId")
        assertEquals(HttpStatusCode.NotFound, getDeletedResponse.status)
    }

    // Add similar tests for users if schema is available

}
