package com.jozuo.kotlin.analysis.gitlab.model

import com.jozuo.kotlin.analysis.Env
import org.springframework.beans.factory.annotation.Autowired

class CommitComment(commit: Commit, private val line: String) {

    @Autowired
    private lateinit var env: Env

    val filePath: String
    val lineNo: Int
    private val message: String
    private var level: String? = null
    private var ruleName: String? = null
    private var ruleUrl: String? = null

    val commitHash = commit.hash

    init {
        if (line.split(",").size < 4) {
            throw IllegalArgumentException("line: [$line] is illegal.")
        }

        filePath = removeComma(extract(1))
        lineNo = Integer.parseInt(extract(2))
        message = removeComma(extract(3))

        if (isDefine(line, 4)) {
            level = removeComma(extract(4))
        }
        if (isDefine(line, 5)) {
            ruleName = removeComma(extract(5))
        }
        if (isDefine(line, 6)) {
            ruleUrl = removeComma(extract(6))
        }
    }

    fun isInModifiedLine(diffInfoList: DiffInfoList): Boolean = diffInfoList.isModifiedLine(filePath, lineNo)

    fun getIndividualMessage() = "${getLevelIcon()}${getContents()}${getRuleLink()}"

    fun getSummaryMessage() = "1. [${getLevelIcon()}${getContents()}](${getGitLabBlobUrl()})${this.getRuleLink()}"

    private fun extract(index: Int) = line.split(",")[index].trim()

    private fun removeComma(str: String) = str.replace("^\"".toRegex(), "").replace("\"$".toRegex(), "")

    private fun getContents() = message + if (ruleName != null) "(${this.ruleName})" else ""

    private fun getRuleLink() = ruleUrl?.let { " [:blue_book:]($ruleUrl)" } ?: ""

    private fun isDefine(line: String, index: Int) =
            (line.split(",").size >= (index + 1) && line.split(",")[index].isNotBlank())

    private fun getLevelIcon(): String {
        val warning = ":warning: "
        val error = ":no_entry_sign: "

        level ?: return error
        return if (level!!.toLowerCase() == "warning") warning else error
    }

    private fun getGitLabBlobUrl() = "${env.getGitLabUrl()}/blob/$commitHash/$filePath/#L$lineNo"
}