package ru.chikchik.tools.cf.service

import org.apache.commons.lang.text.StrSubstitutor
import org.slf4j.LoggerFactory
import ru.chikchik.tools.cf.domain.Profile
import java.io.File
import java.nio.file.Paths
import java.util.*

class FileConfigProducer(private val templateFileName: String): ConfigProducer {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun produce(profiles: List<Profile>, variables: Map<String, String>,
                outputFileFormat: String, outputPath: String): Optional<List<File>> {

        log.info("[~] product config files based on profiles (${profiles.size})..")

        val outputDirectory = File(outputPath)

        if (!outputDirectory.exists()) { outputDirectory.mkdir() }

        val templateFile = File(templateFileName)
        val template = templateFile.readText()

        return if (templateFile.exists()) {

            val results = arrayListOf<File>()

            profiles.forEach { profile ->

                val profileVariables = hashMapOf<String, String>()
                profileVariables.putAll(variables)
                profile.variables.forEach { key, value -> profileVariables[key] = value }

                val outputFileName = StrSubstitutor(profileVariables).replace(outputFileFormat)

                val outputFile = Paths.get(outputPath, outputFileName).toFile()

                outputFile.writeText(
                    StrSubstitutor(profileVariables).replace(template)
                )

                results += outputFile
            }

            Optional.of(results)

        } else {
            log.error("template file wasn't found '$templateFile'")
            Optional.empty()
        }
    }
}