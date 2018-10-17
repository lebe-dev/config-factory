package ru.chikchik.tools.cf.domain

data class Profile(
    val name: String,

    val variables: Map<String, String>
)