package com.jozuo.kotlin.analysis.git.model

import com.jozuo.kotlin.analysis.Env
import com.jozuo.kotlin.analysis.git.repository.GitRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
class ChangeFileListBuilderImpl: ChangeFileListBuilder {

    @Autowired
    private lateinit var env: Env

    @Autowired
    private lateinit var repository: GitRepository

    override var changeFiles = mutableListOf<ChangeFile>()

    private val names = mutableListOf<String>()

    override fun build(): ChangeFileList {
        val commitHashes = repository.getCommits(env.getCommitHashBegin(), env.getCommitHashEnd())
        commitHashes.forEachIndexed { index, commitHash ->
            val from = if (index == commitHashes.size - 1) env.getCommitHashBegin() else commitHashes[index + 1]
            val lines = repository.getDiffFiles(from, commitHash)

            lines.filter(String::isNotEmpty)
                    .map { it -> ChangeFile(commitHash, it.split("\t")[1], it.split("\t")[0]) }
                    .forEach { add(it) }
        }

        return ChangeFileList(this)
    }

    private fun add(changeFile: ChangeFile) {
        if (changeFile.status == "D") return
        if (names.contains(changeFile.name)) return
        changeFiles.add(changeFile)
        names.add(changeFile.name)
    }
}