package ssg

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess


object Files {

    val pagesDir: Path = Paths.get("").resolve("src/main/resources/pages")
    private val baseFile: Path = pagesDir.parent.resolve("base.html")


    fun loadBaseFileContent(): String {
        return loadFileContent(baseFile)
    }

    private fun loadFileContent(path: Path): String {
        val file = path.toFile()
        if (!file.exists()) {
            println("$path not found")
            exitProcess(3)
        }
        return file.readText()
    }


    fun loadFile(path: Path): File {
        val file = path.toFile()
        if (!file.exists()) {
            println("$path not found")
            exitProcess(4)
        }
        return file
    }

    fun loadPageFiles(): Array<File> {
        val pagesDir = pagesDir.toFile()
        if (!pagesDir.exists()) {
            println("$pagesDir not found")
            exitProcess(1)
        }

        if (!pagesDir.isDirectory) {
            println("$pagesDir is not a directory")
            exitProcess(2)
        }

        return pagesDir.listFiles()!!
    }

    fun createPublicDir(): File {
        val publicDir = File("public")
        if (!publicDir.exists()) {
            println("Creating directory ${publicDir.absolutePath}")
            publicDir.mkdir()
        }
        return publicDir
    }
}
