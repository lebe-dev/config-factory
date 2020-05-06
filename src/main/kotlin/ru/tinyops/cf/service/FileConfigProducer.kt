package ru.tinyops.cf.service

import arrow.core.Either
import org.apache.commons.lang.text.StrSubstitutor
import org.slf4j.LoggerFactory
import ru.tinyops.cf.domain.OperationError
import ru.tinyops.cf.domain.OperationResult
import ru.tinyops.cf.domain.Profile
import java.io.File
import java.nio.file.Paths

class FileConfigProducer(private val templateFileName: String): ConfigProducer {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun produce(profiles: List<Profile>, variables: Map<String, String>,
                         outputFileFormat: String, outputPath: String): OperationResult<List<File>> {

        log.info("[~] product config files based on profiles (${profiles.size})..")

        val outputDirectory = File(outputPath)

        if (!outputDirectory.exists()) { outputDirectory.mkdir() }

        val templateFile = File(templateFileName)

        return if (templateFile.exists()) {
            val template = templateFile.readText()

            val results = arrayListOf<File>()

            profiles.forEach { profile ->

                val profileVariables = hashMapOf<String, String>()
                profileVariables.putAll(variables)
                profile.variables.forEach { (key, value) -> profileVariables[key] = value }

                val outputFileName = StrSubstitutor(profileVariables).replace(outputFileFormat)

                val outputFile = Paths.get(outputPath, outputFileName).toFile()

                outputFile.writeText(
                    StrSubstitutor(profileVariables).replace(template)
                )

                results += outputFile
            }

            Either.right(results)

        } else {
            log.error("template file wasn't found '$templateFile'")
            Either.left(OperationError.NOT_FOUND)
        }
    }
}
