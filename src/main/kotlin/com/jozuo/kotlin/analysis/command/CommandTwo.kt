package com.jozuo.kotlin.analysis.command

import com.jozuo.kotlin.analysis.Command
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["command"], havingValue = "two")
class CommandTwo() : Command {

    override fun execute(args: Array<String>) {
        println("CommandOneTwo call")
    }
}

