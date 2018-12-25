package ru.chikchik.tools.cf

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

internal class AppTest {

    @Test
    @DisplayName("Generate configs")
    fun main() {
        val workDir = Paths.get(System.getProperty("user.dir"))

        val templateFileName = "test.template"

        val sourceTemplateFile = File(javaClass.getResource("/$templateFileName").toURI())
        val templateFile = Paths.get(workDir.toString(), templateFileName).toFile()
        sourceTemplateFile.copyTo(templateFile, true)

        val sourceConfigFile = File(javaClass.getResource("/${App.CONFIG_FILE}").toURI())
        val configFile = File(App.CONFIG_FILE)
        sourceConfigFile.copyTo(configFile, true)

        val outputDirName = "test-results"

        App.main(arrayOf("-t", templateFileName, "-o", outputDirName))

        val outputDirectory = Paths.get(workDir.toString(), outputDirName)

        assertTrue(outputDirectory.toFile().exists())

        val firstProfileFile = Paths.get(workDir.toString(), outputDirName, "bristol.com.uk.conf").toFile()
        val expectedProfile1 = File(javaClass.getResource("/expected-profile1.conf").toURI())
        assertTrue(firstProfileFile.exists())
        assertEquals(expectedProfile1.readText(), firstProfileFile.readText())

        val secondProfileFile = Paths.get(workDir.toString(), outputDirName, "krakow.cities.pl.conf").toFile()
        val expectedProfile2 = File(javaClass.getResource("/expected-profile2.conf").toURI())
        assertTrue(secondProfileFile.exists())
        assertEquals(expectedProfile2.readText(), secondProfileFile.readText())

        templateFile.delete()
        outputDirectory.toFile().deleteRecursively()
    }
}