package net.raquezha.ghost

import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersEndpointTest {
    @Test
    fun `should create, read, update, and delete user when valid user is posted`() = testApplication {
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
}
