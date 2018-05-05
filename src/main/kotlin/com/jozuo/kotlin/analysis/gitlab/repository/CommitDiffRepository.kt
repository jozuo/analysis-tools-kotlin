package com.jozuo.kotlin.analysis.gitlab.repository

import com.google.gson.reflect.TypeToken
import com.jozuo.kotlin.analysis.Env
import com.jozuo.kotlin.analysis.helper.RequestHelper
import com.jozuo.kotlin.analysis.repository.ApiCallRepository
import okhttp3.Request
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.springframework.beans.factory.annotation.Autowired

class CommitDiffRepository : ApiCallRepository() {

    @Autowired
    private lateinit var env: Env

    @Autowired
    private lateinit var helper: RequestHelper

    // TODO レスポンスをドメインオブジェクトにする
    fun getDiffInfoList(commitHash: String) {
        Thread.sleep(200)

        val request = Request.Builder()
                .url("${env.getGitLabAPIEndPoint()}/repository/commits/$commitHash/diff")
                .header("PRIVATE-TOKEN", env.getGitLabToken())
                .get()
                .build()

        val type = object : TypeToken<List<Response>>() {}.type
        val diffs = toResponse<List<Response>>(helper.execute(request), type)
    }

    data class Response(
            var diff: String? = null,
            var newPath: String? = null)
}

