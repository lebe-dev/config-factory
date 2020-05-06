package ru.tinyops.cf.service

import ru.tinyops.cf.domain.Profile
import java.io.File
import java.util.*

interface ConfigProducer {
    fun produce(profiles: List<Profile>, variables: Map<String, String>,
                outputFileFormat: String, outputPath: String): Optional<List<File>>
}
