package com.jozuo.kotlin.analysis.sample

import org.springframework.stereotype.Component

@Component
class Hoge {
    fun getEnv(envKey: String): String {
        return System.getenv(envKey) ?:
        throw IllegalStateException("$envKey is not defined.")
    }
}