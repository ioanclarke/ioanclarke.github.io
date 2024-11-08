package ssg

import java.io.File
import java.nio.file.Path


object Generator {

    private val base: String = Files.loadBaseFileContent()

    fun generate(path: Path) {
        println("Regenerating...")

        val publicDir: File = Files.createPublicDir()

        val pages: Array<File> = Files.loadPageFiles()

        pages.forEach {
            val replaced = base.replace("{{content}}", it.readText())
            File(publicDir, it.name).writeText(replaced)
            println("Regenerated ${it.name}")
        }
    }
}
