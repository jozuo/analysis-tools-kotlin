package com.jozuo.kotlin.analysis.git.command

import com.jozuo.kotlin.analysis.Command
import com.jozuo.kotlin.analysis.git.service.DiffFileService
import org.apache.commons.cli.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["command"], havingValue = "diff-file")
class DiffFileCommand: Command {

    @Autowired
    private lateinit var service: DiffFileService

    private lateinit var options: Options

    override fun execute(args: Array<String>): Boolean {
        options = Options()
        options.addOption(null, "command", true, "Exec Command")
        options.addOption("o", "output", true, "Output Directory")
        options.addOption("h", "help", false, "Help Message")

        val cl: CommandLine?
        try {
            cl = DefaultParser().parse(options, args)
        } catch (e: UnrecognizedOptionException) {
            println(e.message)
            return printUsage()
        }
        cl?:return false

        if (cl.hasOption("h") || !cl.hasOption("o")) {
            return printUsage()
        }

        service.execute(cl.getOptionValue("o"))
        return true
    }

    private fun printUsage(): Boolean {
        println(HelpFormatter().printHelp(
                "java -jar analysis.jar --command=diff-file [-o <arg>]", options))
        return false
    }
}