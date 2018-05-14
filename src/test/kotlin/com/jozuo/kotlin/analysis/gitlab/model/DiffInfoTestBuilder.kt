package com.jozuo.kotlin.analysis.gitlab.model

class DiffInfoTestBuilder(
        override var filePath: String,
        override var ranges: List<Range>) : DiffInfoBuilder {

    override fun build() = DiffInfo(this)
}

