package net.raquezha.ghost

/**
 * Exception thrown when one or more required environment variables are missing.
 * Document this class in your wiki for consistent error handling.
 */
class MissingEnvVarException(varNames: List<String>) : RuntimeException(
    "Required environment variable(s) missing: ${varNames.joinToString(", ")}. Please set them in your .env file."
)
