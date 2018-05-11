package com.jozuo.kotlin.analysis.gitlab.model

class DiffInfo(builder: DiffInfoBuilder) {
    var filePath: String = builder.filePath
        private set

    var ranges: List<Range> = builder.ranges
        private set

    fun isModifiedLine(lineNo: Int) = ranges.any { it.isInside(lineNo) }
}