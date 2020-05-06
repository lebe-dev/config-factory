package ru.tinyops.cf.domain

data class Profile(
    val name: String,

    val variables: Map<String, String>
)
