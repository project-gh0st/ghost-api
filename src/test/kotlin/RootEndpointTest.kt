package net.raquezha.ghost

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RootEndpointTest {
    @Test
    fun `should return status ok when root endpoint is called`() = testApplication {
        application {
            configureDatabases(
                jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
                driver = "org.h2.Driver",
                user = "sa",
                password = ""
            )
            module()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
