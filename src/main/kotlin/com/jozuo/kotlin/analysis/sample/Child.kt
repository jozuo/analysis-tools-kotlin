package com.jozuo.kotlin.analysis.sample

import org.springframework.stereotype.Component

@Component
class Child {
    fun get(value: String): String {
        return value
    }
}