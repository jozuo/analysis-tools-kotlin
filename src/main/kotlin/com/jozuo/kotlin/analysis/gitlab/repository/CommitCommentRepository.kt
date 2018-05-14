package com.jozuo.kotlin.analysis.gitlab.repository

import com.jozuo.kotlin.analysis.Env
import com.jozuo.kotlin.analysis.gitlab.model.Commit
import com.jozuo.kotlin.analysis.gitlab.model.CommitComment
import com.jozuo.kotlin.analysis.helper.RequestHelper
import com.jozuo.kotlin.analysis.repository.ApiCallRepository
import okhttp3.FormBody
import okhttp3.Request
import org.springframework.beans.factory.annotation.Autowired

class CommitCommentRepository : ApiCallRepository() {

    @Autowired
    private lateinit var env: Env

    @Autowired
    private lateinit var helper: RequestHelper

    fun postIndividualComment(commitComment: CommitComment) {
        post(
                commitComment.commitHash,
                commitComment.getIndividualMessage(),
                commitComment.filePath,
                commitComment.lineNo)
    }

    fun postSummaryComment(commit: Commit) {
        val summaryMessage = commit.getSummaryMessage()
        summaryMessage ?: return
        post(commit.hash, summaryMessage)
    }


    private fun post(commitHash: String, comment: String, filePath: String? = null, lineNo: Int? = null) {
        Thread.sleep(200)

        val body = FormBody.Builder()
                .add("note", comment)
                .add("path", filePath ?: "")
                .add("line", Integer.toString(lineNo ?: -1))
                .add("line_type", "new")
                .build()

        val request = Request.Builder()
                .url("${env.getGitLabAPIEndPoint()}/repository/commits/$commitHash/comments")
                .header("PRIVATE-TOKEN", env.getGitLabToken())
                .post(body)
                .build()

        helper.execute(request)
    }
}