package com.jozuo.kotlin.analysis.git.repository

import com.jozuo.kotlin.analysis.Env
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

@Repository
class GitRepository {

    @Autowired
    private lateinit var env: Env

    fun getDiffFiles(commitHashBegin: String, commitHashEnd: String): List<String> {
        val result = execute("git diff --name-status $commitHashBegin..$commitHashEnd")
        result ?: return listOf()
        return result.split("\n").filter(String::isNotEmpty)
    }

    fun getCommits(commitHashBegin: String, commitHashEnd: String): List<String> {
        val result = execute("git log --pretty=format:%H $commitHashBegin..$commitHashEnd")
        result ?: return listOf()
        return result.split("\n").filter(String::isNotEmpty)
    }

    private fun execute(cmd: String): String? {
        val processBuilder = ProcessBuilder(cmd.split(" "))
        processBuilder.directory(File(env.getProjectRoot()))
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()

        process.inputStream.use({
            val builder = StringBuilder()
            storeInputStream(builder, it)
            process.waitFor()

            return when (process.exitValue()) {
                0 -> builder.toString()
                else -> throw IllegalStateException(builder.toString())
            }
        })
    }

    private fun storeInputStream(builder: StringBuilder, stream: InputStream) {
        BufferedReader(InputStreamReader(stream)).use({
            while (true) {
                builder.append(it.readLine() ?: break)
                builder.append("\n")
            }
        })
    }
}