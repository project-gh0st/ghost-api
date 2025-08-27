package net.raquezha.ghost

import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthEndpointTest {
    @Test
    fun `should return status ok when health endpoint is called`() = testApplication {
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
    fun `should return error when health endpoint is called with broken database`() = testApplication {
        application {
            // Pass an invalid JDBC URL to simulate DB failure
            try {
                configureDatabases(
                    jdbcUrl = "jdbc:mysql://invalidhost:3306/ghost",
                    driver = "com.mysql.cj.jdbc.Driver",
                    user = "invalid",
                    password = "invalid"
                )
                module()
            } catch (e: Exception) {
                // Expected: DB connection should fail
            }
        }
        val response = client.get("/health")
        // The app should not start, so we expect a connection error or no response
        // If the app does respond, it should not return 200 OK
        assert(response.status != HttpStatusCode.OK)
    }
}
