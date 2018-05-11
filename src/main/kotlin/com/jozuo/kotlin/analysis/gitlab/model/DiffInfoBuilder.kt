package com.jozuo.kotlin.analysis.gitlab.model

interface DiffInfoBuilder {

    var filePath: String
    var ranges: List<Range>

    fun build(): DiffInfo
}