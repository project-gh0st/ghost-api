package net.raquezha.ghost

import io.github.cdimascio.dotenv.Dotenv

class EnvHelper(requiredVars: List<String>) {
    companion object {
        const val DB_HOST = "DB_HOST"
        const val DB_PORT = "DB_PORT"
        const val DB_NAME = "DB_NAME"
        const val DB_USER = "DB_USER"
        const val DB_PASSWORD = "DB_PASSWORD"
        val REQUIRED_DB_VARS = listOf(DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD)
    }

    private val dotenv: Dotenv = Dotenv.load()
    private val missingVars: List<String> = requiredVars.filter { dotenv[it] == null }

    init {
        if (missingVars.isNotEmpty()) {
            throw MissingEnvVarException(missingVars)
        }
    }

    fun get(varName: String): String = dotenv[varName]!!
}
