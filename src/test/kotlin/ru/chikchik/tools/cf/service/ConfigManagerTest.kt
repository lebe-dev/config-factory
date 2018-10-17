package ru.chikchik.tools.cf.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

internal class ConfigManagerTest {
    lateinit var configManager: ConfigManager

    @BeforeEach
    fun setUp() {
        configManager = ConfigManager()
    }

    @Test
    @DisplayName("Load config from file")
    fun load() {
        val configFile = File(javaClass.getResource("/config-factory.conf").toURI())

        val config = configManager.load(configFile).get()

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
        assertEquals("\${identity}.\${domain}.conf", config.outputFileFormat)
    }

    @Test
    @DisplayName("Load config from nonexistence file")
    fun loadFromUnknownFile() {
        assertFalse(configManager.load(File("gq3948ghq9gihdfg")).isPresent)
    }

    @Test
    @DisplayName("Load config from invalid file")
    fun loadFromInvalidFile() {
        val configFile = File("invalid-config")
        configFile.writeText("owrijg0q394jg834jg")

        assertFalse(configManager.load(configFile).isPresent)

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

        assertFalse(configManager.load(configFile).isPresent)

        if (configFile.exists()) { configFile.delete() }
    }

    @Test
    @DisplayName("Load profile without name property")
    fun loadProfileWithoutNameSpecified() {
        val configFile = File("blank-config")
        configFile.writeText("profile {\n" +
                "  tenantId = \"BRISTOL07\"\n" +
                "}")

        assertFalse(configManager.load(configFile).isPresent)

        if (configFile.exists()) { configFile.delete() }
    }
}