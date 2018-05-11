package com.jozuo.kotlin.analysis.gitlab.model

import com.jozuo.kotlin.analysis.gitlab.repository.CommitDiffRepository
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
class DiffInfoBuilderImpl : DiffInfoBuilder {

    override lateinit var filePath: String
    override lateinit var ranges: List<Range>

    private lateinit var entity: CommitDiffRepository.Entity

    fun setEntity(entity: CommitDiffRepository.Entity): DiffInfoBuilderImpl {
        this.entity = entity
        return this
    }

    override fun build(): DiffInfo {
        filePath = entity.newPath!!
        ranges = Regex("@@ -(\\d+?),(\\d+?) \\+(\\d+?),(\\d+?) @@")
                .findAll(entity.diff ?: "")
                .map {
                    val match = Regex("\\+(\\d+?),(\\d+?) ").find(it.value)!!.value
                    val begin = Integer.parseInt(match.split(",")[0].replace("+", "").trim())
                    val end = begin + Integer.parseInt(match.split(",")[1].trim())
                    Range(begin, end)
                }.toList()
        return DiffInfo(this)
    }
}