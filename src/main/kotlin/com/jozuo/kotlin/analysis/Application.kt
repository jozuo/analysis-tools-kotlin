package com.jozuo.kotlin.analysis

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val context = SpringApplication.run(Application::class.java, *args)
    if (!context.getBean(Command::class.java).execute(args)) {
        System.exit(1)
    }
}
