package com.jozuo.kotlin.analysis.gitlab.model

data class Range(
        private val begin: Int,
        private val end: Int) {

    fun isInside(no: Int) = (begin <= no) && (no <= end)
}