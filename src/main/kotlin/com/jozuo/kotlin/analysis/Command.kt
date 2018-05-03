package com.jozuo.kotlin.analysis

interface Command {
    fun execute(args: Array<String>)
}