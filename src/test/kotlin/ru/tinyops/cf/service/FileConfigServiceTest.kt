package ru.tinyops.cf.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.tinyops.cf.App
import ru.tinyops.cf.domain.OperationError
import ru.tinyops.cf.util.assertError
import ru.tinyops.cf.util.assertLeftResult
import ru.tinyops.cf.util.assertRightResult
import ru.tinyops.cf.util.getRandomText
import java.io.File
import java.nio.file.Files

@DisplayName("Configuration Manager")
internal class FileConfigServiceTest {
    private lateinit var configService: FileConfigService

    @BeforeEach
    fun setUp() {
        configService = FileConfigService()
    }

    @Test
    fun `Return bad-config error if config file syntax is invalid`() {
        val configFile = Files.createTempFile("", "").toFile().apply { writeText(getRandomText()) }

        assertError(configService.load(configFile), OperationError.BAD_CONFIG)

        configFile.delete()
    }

    @Test
    fun `Return not-found error if config file was not found`() {
        assertError(configService.load(File(getRandomText())), OperationError.NOT_FOUND)
    }

    @Test
    fun `Valid config should contain all profiles`() {
        val configFile = File(javaClass.getResource("/${App.CONFIG_FILE}").toURI())

        assertRightResult(configService.load(configFile)) { config ->
            assertEquals(2, config.profiles.size)

            val bristolProfile = config.profiles.first()

            assertEquals("bristol", bristolProfile.name)
            assertEquals("com.uk", bristolProfile.variables["domain"])
            assertEquals("BRISTOL07", bristolProfile.variables["tenantId"])

            val krakowProfile = config.profiles.last()

            assertEquals("krakow", krakowProfile.name)
            assertEquals("cities.pl", krakowProfile.variables["domain"])
            assertEquals("KRAKOW-ID", krakowProfile.variables["tenantId"])

            assertEquals("demosite.com", config.variables["domain"])
            assertEquals("\${name}.\${domain}.conf", config.outputFileFormat)
        }
    }

    @Test
    @DisplayName("Load profile with blank name")
    fun loadProfileWithBlankName() {
        val configFile = File("blank-config")
        configFile.writeText("profile {\n" +
                "  name = \"\"\n" +
                "\n" +
                "  tenantId = \"BRISTOL07\"\n" +
                "}")

        assertLeftResult(configService.load(configFile))

        configFile.delete()
    }

    @Test
    @DisplayName("Load profile without name property")
    fun loadProfileWithoutNameSpecified() {
        val configFile = File("blank-config")
        configFile.writeText("profile {\n" +
                "  tenantId = \"BRISTOL07\"\n" +
                "}")

        assertLeftResult(configService.load(configFile))

        configFile.delete()
    }
}
