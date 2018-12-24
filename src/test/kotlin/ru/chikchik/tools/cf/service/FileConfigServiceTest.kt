package ru.chikchik.tools.cf.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.chikchik.tools.cf.App
import java.io.File

@DisplayName("Configuration Manager")
internal class FileConfigServiceTest {
    private lateinit var configService: FileConfigService

    @BeforeEach
    fun setUp() {
        configService = FileConfigService()
    }

    @Test
    @DisplayName("Load config from file")
    fun load() {
        val configFile = File(javaClass.getResource("/${App.CONFIG_FILE}").toURI())

        val config = configService.load(configFile).get()

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

    @Test
    @DisplayName("Load config from nonexistence file")
    fun loadFromUnknownFile() {
        assertFalse(configService.load(File("gq3948ghq9gihdfg")).isPresent)
    }

    @Test
    @DisplayName("Load config from invalid file")
    fun loadFromInvalidFile() {
        val configFile = File("invalid-config")
        configFile.writeText("owrijg0q394jg834jg")

        assertFalse(configService.load(configFile).isPresent)

        if (configFile.exists()) { configFile.delete() }
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

        assertFalse(configService.load(configFile).isPresent)

        if (configFile.exists()) { configFile.delete() }
    }

    @Test
    @DisplayName("Load profile without name property")
    fun loadProfileWithoutNameSpecified() {
        val configFile = File("blank-config")
        configFile.writeText("profile {\n" +
                "  tenantId = \"BRISTOL07\"\n" +
                "}")

        assertFalse(configService.load(configFile).isPresent)

        if (configFile.exists()) { configFile.delete() }
    }
}