package ru.tinyops.cf.service

import ru.tinyops.cf.domain.OperationResult
import ru.tinyops.cf.domain.Profile
import java.io.File

interface ConfigProducer {
    fun produce(profiles: List<Profile>, variables: Map<String, String>,
                outputFileFormat: String, outputPath: String): OperationResult<List<File>>
}
