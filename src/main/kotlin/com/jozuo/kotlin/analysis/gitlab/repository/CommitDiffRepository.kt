package com.jozuo.kotlin.analysis.gitlab.repository

import com.google.gson.reflect.TypeToken
import com.jozuo.kotlin.analysis.Env
import com.jozuo.kotlin.analysis.gitlab.model.DiffInfo
import com.jozuo.kotlin.analysis.gitlab.model.DiffInfoBuilderImpl
import com.jozuo.kotlin.analysis.helper.RequestHelper
import com.jozuo.kotlin.analysis.repository.ApiCallRepository
import okhttp3.Request
import org.springframework.beans.factory.annotation.Autowired

class CommitDiffRepository : ApiCallRepository() {

    @Autowired
    private lateinit var env: Env

    @Autowired
    private lateinit var helper: RequestHelper

    // TODO レスポンスをドメインオブジェクトにする
    fun getDiffInfoList(commitHash: String): List<DiffInfo> {
        Thread.sleep(200)

        val request = Request.Builder()
                .url("${env.getGitLabAPIEndPoint()}/repository/commits/$commitHash/diff")
                .header("PRIVATE-TOKEN", env.getGitLabToken())
                .get()
                .build()

        val type = object : TypeToken<List<Entity>>() {}.type
        val entities = toResponse<List<Entity>>(helper.execute(request), type)
        return entities.filter {
            it.deletedFile != true
        }.map {
            DiffInfoBuilderImpl().setEntity(it).build()
        }
    }

    data class Entity(
            var diff: String? = null,
            var newPath: String? = null,
            var deletedFile: Boolean? = null)
}

