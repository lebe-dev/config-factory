package ru.tinyops.cf.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.tinyops.cf.App
import ru.tinyops.cf.domain.Profile
import java.io.File

@DisplayName("Config Producer Test")
internal class FileConfigProducerTest {

    @Test
    @DisplayName("Produce config files")
    fun produce() {
        val fileName = "test.template"

        val templateResource = File(javaClass.getResource("/$fileName").toURI())

        val templateFile = templateResource.copyTo(File(fileName), true)

        val producer = FileConfigProducer(templateFile.name)
        val results = producer.produce(
            profiles = listOf(
                Profile(
                    name = "alpha",
                    variables = mapOf(
                        "name" to "alpha",
                        "domain" to "synchrophasotron.com"
                    )
                ),
                Profile(
                    name = "beta",
                    variables = mapOf(
                        "name" to "beta",
                        "domain" to "rocket.com"
                    )
                )
            ),
            variables = mapOf("domain" to "alka.com"),
            outputFileFormat = "\${name}.\${domain}.conf",
            outputPath = App.OUTPUT_DIRECTORY
        )

        assertEquals(2, results.get().size)

        val alphaResult = results.get().first()
        val alphaResultText = alphaResult.readText()

        assertTrue(alphaResultText.contains("server_name  alpha.synchrophasotron.com;"))
        assertEquals("alpha.synchrophasotron.com.conf", alphaResult.name)

        val betaResult = results.get().last()
        val betaResultText = betaResult.readText()

        assertTrue(betaResultText.contains("server_name  beta.rocket.com;"))
        assertEquals("beta.rocket.com.conf", betaResult.name)


        results.get().forEach {
            if (it.exists()) {
                it.delete()
            }
        }

        templateFile.delete()
    }

    @Test
    @DisplayName("Template file doesn't exist")
    fun templateFileDoesNotExist() {
        assertFalse(
            FileConfigProducer("does-not-exist-file").produce(listOf(), mapOf(), "", "").isPresent
        )
    }
}
