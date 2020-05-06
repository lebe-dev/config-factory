package ru.tinyops.cf.service

import ru.tinyops.cf.domain.AppConfig
import java.io.File
import java.util.*

/**
 * Configuration service
 */
interface ConfigService {
    /**
     * Load config from file
     *
     * @param file configuration file
     * @return application config entity, empty - otherwise
     */
    fun load(file: File): Optional<AppConfig>
}
