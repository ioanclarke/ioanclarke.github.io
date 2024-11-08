package ssg

import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import java.io.File


fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            staticFiles("/", File("docs")) {
                default("index.html")
            }
        }
    }.start(wait = true)
}
