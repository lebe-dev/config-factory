package ru.chikchik.tools.cf.service

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import ru.chikchik.tools.cf.domain.AppConfig
import ru.chikchik.tools.cf.domain.Profile
import java.io.File
import java.nio.file.Paths
import java.util.*

class FileConfigService: ConfigService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun load(file: File): Optional<AppConfig> =
        if (file.exists()) {
            try {
                log.info("[~] loading config from file '${file.name}'..")
                log.info("  - path: '${file.absolutePath}'")

                val config = ConfigFactory.parseFile(file).getConfig("config")

                val globalVariableNames = getStringList(config, "variableNames")

                val result = AppConfig(
                    variableNames = globalVariableNames.distinct(),
                    variables = getVariableMap(globalVariableNames, config),
                    profiles = getProfileList(globalVariableNames),
                    outputFileFormat = config.getString("outputFileFormat")
                )

                log.info("+ config successfully loaded")
                log.debug(result.toString())

                Optional.of(result)

            } catch (e: ConfigException.Parse) {
                log.error("invalid configuration file syntax: ${e.message}", e)
                Optional.empty<AppConfig>()

            } catch (e: Exception) {
                log.error("unable to read configuration from file: ${e.message}", e)
                Optional.empty<AppConfig>()
            }

        } else {
            log.error("config file '${file.name}' wasn't found")
            Optional.empty()
        }

    internal fun loadProfileFromFile(globalVariableNames: List<String>, file: File): Optional<Profile> =
        try {
            log.info("~ loading profile from '${file.name}'..")

            val results = HashMap<String, String>()
            val config = ConfigFactory.parseFile(file).getConfig("profile")

            globalVariableNames.forEach { variableName ->
                if (config.hasPath(variableName)) {
                    results += variableName to config.getString(variableName)
                }
            }

            val result = Profile(
                name = config.getString("name"),
                variables = results
            )

            log.info("+ profile '${file.name}' has been loaded")
            log.debug(result.toString())

            Optional.of(result)

        } catch (e: ConfigException.Parse) {
            log.error("invalid file syntax: ${e.message}", e)
            Optional.empty()

        } catch (e: Exception) {
            log.error("unable to read profile from file: ${e.message}", e)
            Optional.empty()
        }

    private fun getProfileList(globalVariableNames: List<String>): List<Profile> {
        val results = ArrayList<Profile>()

        val profilesPath = Paths.get("profiles")

        if (profilesPath.toFile().exists()) {
            val profileFiles = profilesPath.toFile().listFiles().orEmpty()

            profileFiles.forEach { profileFile ->
                val profile = loadProfileFromFile(globalVariableNames, profileFile)

                if (profile.isPresent) {
                    results += profile.get()
                }
            }
        }

        return results
    }

    private fun getVariableMap(variableNames: List<String>, config: Config): Map<String, String> =
            variableNames.asSequence()
                         .filter { config.hasPath(it) }
                         .map { it to config.getString(it) }.toMap()

    private fun getStringList(config: Config, propertyName: String): List<String> =
            if (config.hasPath(propertyName)) config.getStringList(propertyName) else listOf()
}