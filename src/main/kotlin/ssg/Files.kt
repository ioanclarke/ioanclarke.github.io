package ssg

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*
import kotlin.system.exitProcess


object Files {

    private val assetFileNames: List<String> = listOf("styles.css", "favicon.ico")
    val resourcesDir: Path = Paths.get("").resolve("src/main/resources")
    val pagesDir: Path = Paths.get("").resolve(resourcesDir).resolve("pages")
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

    @OptIn(ExperimentalPathApi::class)
    fun loadPageFiles(): Sequence<Path> {
        if (!pagesDir.exists()) {
            println("$pagesDir not found")
            exitProcess(1)
        }

        if (!pagesDir.isDirectory()) {
            println("$pagesDir is not a directory")
            exitProcess(2)
        }

        return pagesDir.walk()
    }

    fun loadAssets(): List<Path> {
        val paths = assetFileNames.map { resourcesDir.resolve(it) }

        paths.forEach {
            if (!it.exists()) {
                println("$it asset not found")
                exitProcess(5)
            }
        }

        return paths
    }

    @OptIn(ExperimentalPathApi::class)
    fun createDocsDir(): Path {
        val docsDir = Path("docs")
        if (docsDir.exists()) {
            println("Deleting existing directory ${docsDir.toAbsolutePath()}")
            docsDir.deleteRecursively()
        }
        return docsDir.createDirectory()
    }
}
