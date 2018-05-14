package com.jozuo.kotlin.analysis.gitlab.model

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(Enclosed::class)
class CommitTest {

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class getter {
        @Test
        fun hash() {
            val commit: Commit = createCommit("commit hash")
            assertThat(commit.hash, `is`("commit hash"))
        }
    }

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class add {

        private val commit: Commit = createCommit("commit hash")

        @Test
        fun 要素が追加された場合() {
            // prepare
            val field = Commit::class.java.getDeclaredField("commitComments")
            field!!.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val commitComments = field.get(commit) as List<CommitComment>

            // test
            // - 初期状態
            assertThat(commitComments.size, `is`(0))
            // - 1件目追加
            commit.add("\"commit-hash\",\"file path\", 123, \"comment message\"")
            assertThat(commitComments.size, `is`(1))
            // - 2件目追加
            commit.add("\"commit-hash\",\"file path\", 123, \"comment message\"")
            assertThat(commitComments.size, `is`(2))
        }
    }
}

fun createCommit(commitHash: String, diffInfoList: DiffInfoList? = null): Commit {
    val list = if (diffInfoList == null) {
        val ranges = listOf(Range(10, 20), Range(30, 40))
        DiffInfoList(listOf(DiffInfo(DiffInfoTestBuilder("path string", ranges))))
    } else {
        diffInfoList
    }
    return Commit(commitHash, list)
}

