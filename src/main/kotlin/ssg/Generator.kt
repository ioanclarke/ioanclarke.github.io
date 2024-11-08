package ssg

import java.io.File
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchEvent


object Generator {

    private val base: String = Files.loadBaseFileContent()

    fun generate(path: Path, eventKind: WatchEvent.Kind<Path>) {
        println("Regenerating...")

        val publicDir: File = Files.createPublicDir()

//        val pages: List<File> = Files.loadPageFiles()

        val page = Files.loadFile(path)

        val updatedFile = File(publicDir, page.name)

        if (page.isDirectory) {
            if (eventKind == ENTRY_CREATE) {
                if (!updatedFile.exists()) {
                    updatedFile.mkdir()
                    println("Created directory ${path.fileName}")
                }
            } else if (eventKind == ENTRY_DELETE) {
                updatedFile.deleteRecursively()
                println("Deleted directory ${path.fileName}")
            }
        } else {
            if (eventKind == ENTRY_CREATE || eventKind == ENTRY_MODIFY) {
                val replaced = base.replace("{{content}}", page.readText())
                updatedFile.writeText(replaced)
                println("Regenerated ${path.fileName}")
            } else if (eventKind == ENTRY_DELETE) {
                updatedFile.delete()
                println("Deleted ${path.fileName}")
            }
        }

//        pages.forEach {
//            val file = File(publicDir, it.name)
//            if (it.isDirectory) {
//                if (eventKind == ENTRY_CREATE) {
//                    if (!file.exists()) {
//                        file.mkdir()
//                        println("Created directory ${it.name}")
//                    }
//                } else if (eventKind == ENTRY_DELETE) {
//                    file.deleteRecursively()
//                    println("Deleted directory ${it.name}")
//                }
//            } else {
//                if (eventKind == ENTRY_CREATE || eventKind == ENTRY_MODIFY) {
//                    val replaced = base.replace("{{content}}", it.readText())
//                    file.writeText(replaced)
//                    println("Regenerated ${it.name}")
//                } else if (eventKind == ENTRY_DELETE) {
//                    file.delete()
//                    println("Deleted ${it.name}")
//                }
//            }
//        }
    }
}
