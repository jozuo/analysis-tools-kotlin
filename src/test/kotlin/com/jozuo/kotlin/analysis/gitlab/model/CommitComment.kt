package com.jozuo.kotlin.analysis.gitlab.model

class CommitComment(private val commit: Commit, private val line: String) {

    private val filePath = removeComma(extract(1))
    private val lineNo = Integer.parseInt(extract(2))
    private val message = removeComma(extract(3))
    private var level: String? = null
    private var ruleName: String? = null
    private var ruleUrl: String? = null

    val commitHash = commit.hash

    init {
        if (line.split(",").size < 4) throw IllegalArgumentException("line: [$line]")
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

    fun isModifiedLine(diffInfoList: DiffInfoList): Boolean = diffInfoList.isModifiedLine(filePath, lineNo)



    private fun extract(index: Int) = line.split(",")[index]

    private fun removeComma(str: String): String = str.trim().replace("^\"", "").replace("\"$", "")

    private fun isDefine(line: String, index: Int) =
            (line.split(",").size >= (index + 1) && line.split(",")[index].isNotBlank())
}