package io.truthencode.gradle.plugin.taxi

import java.io.File
import java.io.InputStream
import java.net.URISyntaxException

object FileResourceUtil {
    // get a file from the resources folder
// works everywhere, IDEA, unit test and JAR file.
    fun getFileFromResourceAsStream(fileName: String): InputStream {
        // The class loader that loaded the class
        val classLoader = javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream(fileName)

        // the stream holding the file content
        return inputStream ?: throw IllegalArgumentException("file not found! $fileName")
    }

    /*
        The resource URL is not working in the JAR
        If we try to access a file that is inside a JAR,
        It throws NoSuchFileException (linux), InvalidPathException (Windows)

        Resource URL Sample: file:java-io.jar!/json/file1.json
     */
    @Throws(URISyntaxException::class)
    fun getFileFromResource(fileName: String): File {
        val classLoader = javaClass.classLoader
        val resource = classLoader.getResource(fileName)
        return when {
            resource == null -> throw IllegalArgumentException("file not found! $fileName")
            else -> {
                // failed if files have whitespaces or special characters
                // return File(resource.file)

                File(resource.toURI())
            }
        }
    }
}
