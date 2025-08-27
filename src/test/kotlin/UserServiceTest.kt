package net.raquezha.ghost

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserServiceTest {
    @Test
    fun `should return user when read is called with valid id`() = runBlocking {
        val mockService = mockk<UserService>()
        val expectedUser = ExposedUser("Alice", 30)
        coEvery { mockService.read(1) } returns expectedUser

        val user = mockService.read(1)
        assertEquals(expectedUser, user)
        coVerify { mockService.read(1) }
    }

    @Test
    fun `should return null when read is called with missing id`() = runBlocking {
        val mockService = mockk<UserService>()
        coEvery { mockService.read(999) } returns null

        val user = mockService.read(999)
        assertNull(user)
        coVerify { mockService.read(999) }
    }
}
