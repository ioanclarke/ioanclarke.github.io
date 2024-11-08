package ssg

import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchKey
import java.nio.file.WatchService


fun main() {
    watchFiles()
    embeddedServer(Netty, port = 8080) {
        routing {
            // Serve static files from the "static" directory
            staticFiles("/", File("public")) {
                default("index.html")
            }
        }
    }.start(wait = true)
}

@OptIn(DelicateCoroutinesApi::class)
fun watchFiles() {
    GlobalScope.launch {
        val watchDir: Path = Files.pagesDir
        val watchService: WatchService = FileSystems.getDefault().newWatchService()

        watchDir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)

        println("Watching directory: ${watchDir.toAbsolutePath()}")

        while (true) {
            val key: WatchKey = watchService.take()

            for (event in key.pollEvents()) {
                val kind = event.kind()

                if (kind == OVERFLOW) {
                    continue
                }

                val filePath = event.context() as Path

                // Display event details
                when (kind) {
                    ENTRY_CREATE -> println("File created: $filePath")
                    ENTRY_DELETE -> println("File deleted: $filePath")
                    ENTRY_MODIFY -> println("File modified: $filePath")
                }
                Generator.generate(filePath)
            }

            // Reset key to allow further events to be captured
            val valid = key.reset()
            if (!valid) {
                println("Watch key no longer valid!")
                break
            }
        }

    }
}
