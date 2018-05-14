package com.jozuo.kotlin.analysis.gitlab.model

import com.jozuo.kotlin.analysis.Env
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

@RunWith(Enclosed::class)
class CommitCommentTest {

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class constructor {

        private val commit = Commit("commit hash string")

        @Rule
        @JvmField
        final val thrown = ExpectedException.none()!!

        @Test
        fun 引数が不正な場合1() {
            // prepare
            val line = "commit-comment"
            thrown.expect(IllegalArgumentException::class.java)
            thrown.expectMessage("line: [$line] is illegal.")

            // test
            CommitComment(commit, line)
        }

        @Test
        fun 引数が不正な場合2() {
            // prepare
            val line = "commit-comment,path"
            thrown.expect(IllegalArgumentException::class.java)
            thrown.expectMessage("line: [$line] is illegal.")

            // test
            CommitComment(commit, line)
        }

        @Test
        fun 引数が不正な場合3() {
            // prepare
            val line = "commit-comment,path,123"
            thrown.expect(IllegalArgumentException::class.java)
            thrown.expectMessage("line: [$line] is illegal.")

            // test
            CommitComment(commit, line)
        }
    }

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class getter {

        @MockBean
        private lateinit var env: Env

        @Autowired
        private lateinit var beanFactory: AutowireCapableBeanFactory

        private val commit = Commit("commit hash string")
        private val line = "\"commit hash\", \"file path\", 123, \"comment message\""
        private var commitComment: CommitComment? = null

        @Before
        fun setup() {
            commitComment = CommitComment(commit, line)
            beanFactory.autowireBean(commitComment)
        }

        @Test
        fun filePath() {
            assertThat(commitComment!!.filePath, `is`("file path"))
        }

        @Test
        fun commitHash() {
            assertThat(commitComment!!.commitHash, `is`("commit hash string"))
        }

        @Test
        fun lineNo() {
            assertThat(commitComment!!.lineNo, `is`(123))
        }

        @Test
        fun getGitLabBlobUrl() {
            // prepare
            `when`(env.getGitLabUrl()).thenReturn("http://localhost/git1")

            // test
            val method = commitComment!!::class.declaredFunctions.find { it.name == "getGitLabBlobUrl" }
            val result = method?.let {
                it.isAccessible = true
                it.call(commitComment)
            }.toString()
            assertThat(result, `is`("http://localhost/git1/blob/commit hash string/file path/#L123"))
        }
    }

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class isModifiedLine {
        private val commit = Commit("commit hash string")
        private val ranges = listOf(Range(10, 20), Range(30, 40))
        private val diffInfoList = DiffInfoList(listOf(DiffInfo(DiffInfoTestBuilder("path string", ranges))))

        @Test
        fun 行番号がDiff範囲1よりも小さい場合() {
            val line = "\"commit hash\",\"path string\", 9, \"comment message\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.isModifiedLine(diffInfoList), `is`(false))
        }

        @Test
        fun 行番号がDiff範囲1の下限の場合() {
            val line = "\"commit hash\",\"path string\", 10, \"comment message\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.isModifiedLine(diffInfoList), `is`(true))
        }

        @Test
        fun 行番号がDiff範囲1の上限の場合() {
            val line = "\"commit hash\",\"path string\", 20, \"comment message\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.isModifiedLine(diffInfoList), `is`(true))
        }

        @Test
        fun 行番号がDiff範囲1の上限よりも大きい場合() {
            val line = "\"commit hash\",\"path string\", 21, \"comment message\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.isModifiedLine(diffInfoList), `is`(false))
        }

        @Test
        fun 行番号がDiff範囲2よりも小さい場合() {
            val line = "\"commit hash\",\"path string\", 29, \"comment message\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.isModifiedLine(diffInfoList), `is`(false))
        }

        @Test
        fun 行番号がDiff範囲2の下限の場合() {
            val line = "\"commit hash\",\"path string\", 30, \"comment message\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.isModifiedLine(diffInfoList), `is`(true))
        }

        @Test
        fun 行番号がDiff範囲2の上限の場合() {
            val line = "\"commit hash\",\"path string\", 40, \"comment message\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.isModifiedLine(diffInfoList), `is`(true))
        }

        @Test
        fun 行番号がDiff範囲2の上限よりも大きい場合() {
            val line = "\"commit hash\",\"path string\", 41, \"comment message\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.isModifiedLine(diffInfoList), `is`(false))
        }

        @Test
        fun Diff情報が存在しないファイルの場合() {
            val line = "\"commit hash\",\"hogehogehoge\", 30, \"comment message\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.isModifiedLine(diffInfoList), `is`(false))
        }
    }

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class getIndividualMessage {

        private val commit = Commit("commit hash string")

        @Test
        fun 最低限の引数の場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.getIndividualMessage(), `is`(":no_entry_sign: comment message"))
        }

        @Test
        fun レベルwarning指定されている場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\", \"warning\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.getIndividualMessage(), `is`(":warning: comment message"))
        }

        @Test
        fun レベルerror指定されている場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\", \"no_entry_sign\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.getIndividualMessage(), `is`(":no_entry_sign: comment message"))
        }

        @Test
        fun レベルで指定値以外が指定されている場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\", \"hoge\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.getIndividualMessage(), `is`(":no_entry_sign: comment message"))
        }

        @Test
        fun ルール名が指定されている場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\", , \"rule name string\""
            val commitComment = CommitComment(commit, line)
            assertThat(commitComment.getIndividualMessage(), `is`(":no_entry_sign: comment message(rule name string)"))
        }

        @Test
        fun ルールURLが指定されている場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\", , ,\"http://localhost/hoge/\""
            val commitComment = CommitComment(commit, line)
            val expected = ":no_entry_sign: comment message [:blue_book:](http://localhost/hoge/)"
            assertThat(commitComment.getIndividualMessage(), `is`(expected))
        }
    }

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class getSummaryMessage {
        @MockBean
        private lateinit var env: Env

        @Autowired
        private lateinit var beanFactory: AutowireCapableBeanFactory

        private val commit = Commit("commit hash string")

        private val gitlabBlobUrl = "http://localhost/git1/blob/commit hash string/file path/#L123"

        @Before
        fun setup() {

            `when`(env.getGitLabUrl()).thenReturn("http://localhost/git1")
        }

        @Test
        fun 最低限の引数の場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\""
            val commitComment = CommitComment(commit, line)
            beanFactory.autowireBean(commitComment)

            val result = commitComment.getSummaryMessage()
            assertThat(result, `is`("1. [:no_entry_sign: comment message]($gitlabBlobUrl)"))
        }

        @Test
        fun レベルwarning指定されている場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\", \"warning\""
            val commitComment = CommitComment(commit, line)
            beanFactory.autowireBean(commitComment)

            val result = commitComment.getSummaryMessage()
            assertThat(result, `is`("1. [:warning: comment message]($gitlabBlobUrl)"))
        }

        @Test
        fun レベルerror指定されている場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\", \"error\""
            val commitComment = CommitComment(commit, line)
            beanFactory.autowireBean(commitComment)

            val result = commitComment.getSummaryMessage()
            assertThat(result, `is`("1. [:no_entry_sign: comment message]($gitlabBlobUrl)"))
        }

        @Test
        fun レベルで指定値以外が指定されている場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\", \"hoge\""
            val commitComment = CommitComment(commit, line)
            beanFactory.autowireBean(commitComment)

            val result = commitComment.getSummaryMessage()
            assertThat(result, `is`("1. [:no_entry_sign: comment message]($gitlabBlobUrl)"))
        }

        @Test
        fun ルール名が指定されている場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\", , \"rule name string\""
            val commitComment = CommitComment(commit, line)
            beanFactory.autowireBean(commitComment)

            val result = commitComment.getSummaryMessage()
            assertThat(result, `is`("1. [:no_entry_sign: comment message(rule name string)]($gitlabBlobUrl)"))
        }

        @Test
        fun ルールURLが指定されている場合() {
            val line = "\"commit-hash\",\"file path\", 123, \"comment message\", , ,\"http://localhost/hoge/\""
            val commitComment = CommitComment(commit, line)
            beanFactory.autowireBean(commitComment)

            val result = commitComment.getSummaryMessage()
            val ruleUrl = "[:blue_book:](http://localhost/hoge/)"
            assertThat(result, `is`("1. [:no_entry_sign: comment message]($gitlabBlobUrl) $ruleUrl"))
        }
    }
}

