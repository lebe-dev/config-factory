package ru.tinyops.cf.util

import java.util.UUID

fun getRandomText() = UUID.randomUUID().toString().take(8)
