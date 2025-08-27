package net.raquezha.ghost

import io.ktor.server.application.*
import io.ktor.util.AttributeKey

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureSecurity()
    configureSerialization()
    if (attributes.getOrNull(AttributeKey<Boolean>("dbConfigured")) != true) {
        configureDatabases()
    }
    configureRouting()
}
