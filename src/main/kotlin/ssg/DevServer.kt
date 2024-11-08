package ssg

import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files as JavaFiles
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.attribute.BasicFileAttributes


fun main() {
//    watchFiles()
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

        registerAll(watchDir, watchService)

        println("Watching directory: ${watchDir.toAbsolutePath()}")

        while (true) {
            val key: WatchKey = watchService.take()

            for (event in key.pollEvents()) {
                val kind = event.kind()

                if (kind == OVERFLOW) {
                    continue
                }

                val filePath = event.context() as Path

                // TODO currently broken because it outputs 'File modified: /Users/ioan.clarke/workspace/hobby/my-ssg/index.html'
                when (kind) {
                    ENTRY_CREATE -> println("File created: ${filePath.toAbsolutePath()}")
                    ENTRY_DELETE -> println("File deleted: ${filePath.toAbsolutePath()}")
                    ENTRY_MODIFY -> println("File modified: ${filePath.toAbsolutePath()}")
                }
                Generator.generate(filePath, kind as WatchEvent.Kind<Path>)
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

private fun registerAll(start: Path, watcher: WatchService) {

    JavaFiles.walkFileTree(start, object : SimpleFileVisitor<Path>() {
        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
            return FileVisitResult.CONTINUE
        }
    })
}
