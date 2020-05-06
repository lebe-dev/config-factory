package ru.tinyops.cf.service

import ru.tinyops.cf.domain.AppConfig
import ru.tinyops.cf.domain.OperationResult
import java.io.File

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
    fun load(file: File): OperationResult<AppConfig>
}
