package ssg

import java.nio.file.Path
import kotlin.io.path.*

val docsDir: Path = Files.createDocsDir()

fun main() {
    writeHtmlFiles()
    writeAssets()
}

fun writeHtmlFiles() {
    val baseHtml: String = Files.loadBaseFileContent()
    val pages: Sequence<Path> = Files.loadPageFiles()

    pages.forEach {
        val relativePath = Files.pagesDir.toAbsolutePath().relativize(it.toAbsolutePath())
        val destination = docsDir.resolve(relativePath)
        val contentReplaced = baseHtml.replace("{{content}}", it.readText())

        val parent = relativePath.parent
        val finished = if (parent != null) {
            val parentDirDestination = docsDir.resolve(parent)
            if (parentDirDestination.notExists()) {
                println("Creating directory $parentDirDestination")
                parentDirDestination.createDirectories()
            }
            contentReplaced.replace("""href="/""", """href="../""")
        } else {
            contentReplaced
        }

        destination.writeText(finished)
        println("Generated $destination")
    }

}

fun writeAssets() {
   Files.loadAssets().forEach {
       val relativePath = Files.resourcesDir.toAbsolutePath().relativize(it.toAbsolutePath())
       val destination = docsDir.resolve(relativePath)
       it.copyTo(destination, overwrite = true)
       println("Copied ${it.name}")
   }
}
