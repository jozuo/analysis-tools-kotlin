package com.jozuo.kotlin.analysis.git.model

interface ChangeFileListBuilder {
    val changeFiles: MutableList<ChangeFile>
    fun build(): ChangeFileList
}