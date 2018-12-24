package ru.chikchik.tools.cf.service

import ru.chikchik.tools.cf.domain.AppConfig
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