package com.jozuo.kotlin.analysis.gitlab.model

class DiffInfoList(val diffInfos: List<DiffInfo>) {

    fun isModifiedLine(filePath: String, lineNo: Int): Boolean {
        val diffInfo = diffInfos.filter{ it.filePath == filePath }
        if (diffInfo.isEmpty()) return false
        return diffInfo[0].isModifiedLine(lineNo)
    }
}
