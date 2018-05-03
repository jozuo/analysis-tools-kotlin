package com.jozuo.kotlin.analysis.git.model

data class ChangeFile(
        val commitHash: String,
        val name: String,
        val status: String)