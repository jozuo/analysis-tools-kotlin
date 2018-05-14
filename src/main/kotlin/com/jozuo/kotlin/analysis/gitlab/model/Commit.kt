package com.jozuo.kotlin.analysis.gitlab.model

import com.jozuo.kotlin.analysis.gitlab.repository.CommitCommentRepository

class Commit(
        val hash: String,
        private val diffInfoList: DiffInfoList) {

     private lateinit var repository: CommitCommentRepository;

    private val commitComments: MutableList<CommitComment> = mutableListOf()

    fun add(line: String) {
        commitComments.add(CommitComment(this, line))
    }

    fun getSummaryMessage(): String? {
        val extraComments = commitComments.filter { !it.isInModifiedLine(diffInfoList) }
        if (extraComments.isEmpty()) {
            return null
        }

        val builder = StringBuilder()
        builder.append("#### ${extraComments.size} extra issue\n")
        builder.append("\n")
        builder.append("Note: The following issues were found on lines that were not modified in the commit. ")
        builder.append("Because these issues can't be reported as line comments, they are summarized here:\n")
        builder.append("\n")
        builder.append(extraComments
                .map { it.getSummaryMessage() }
                .reduce { prev, current -> "$prev\n$current" })

        return builder.toString()
    }

    fun postComment() {
        postIndividualComment();
        repository.postSummaryComment(this);
    }

    private fun postIndividualComment() {
        commitComments.filter { it.isInModifiedLine(diffInfoList) }
                .forEach { repository.postIndividualComment(it) }

    }
}