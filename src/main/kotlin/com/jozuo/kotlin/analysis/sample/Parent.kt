package com.jozuo.kotlin.analysis.sample

import org.springframework.beans.factory.annotation.Autowired

class Parent {

    @Autowired
    private lateinit var child: Child

    fun get(): String {
        return child.get("hogehoge")
    }
}