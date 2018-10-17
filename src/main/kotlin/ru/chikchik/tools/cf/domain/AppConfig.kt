package ru.chikchik.tools.cf.domain

data class AppConfig(
    /**
     * Названия переменных
     */
    val variableNames: List<String> = listOf(),

        /**
     * Переменные и значения
     */
    val variables: Map<String, String>,

    val profiles: List<Profile> = listOf(),

    /**
     * Формат наименования результирующих файлов.
     *
     * Поддерживается использование переменных.
     *
     * Например, ${identity}.${domain}.conf
     */
    val outputFileFormat: String
)