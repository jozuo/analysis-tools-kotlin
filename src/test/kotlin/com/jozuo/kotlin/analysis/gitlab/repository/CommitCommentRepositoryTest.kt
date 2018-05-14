package com.jozuo.kotlin.analysis.gitlab.repository

import com.jozuo.kotlin.analysis.Env
import com.jozuo.kotlin.analysis.gitlab.model.Commit
import com.jozuo.kotlin.analysis.gitlab.model.CommitComment
import com.jozuo.kotlin.analysis.helper.RequestHelper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.times
import okhttp3.FormBody
import okhttp3.Request
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.springframework.test.context.junit4.SpringRunner
import java.io.File


@RunWith(Enclosed::class)
class CommitCommentRepositoryTest {

    @RunWith(PowerMockRunner::class)
    @PrepareForTest(CommitCommentRepository::class, CommitComment::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    class postIndividualComment {

        @Mock
        private lateinit var env: Env

        @Mock
        private lateinit var helper: RequestHelper

        @InjectMocks
        private lateinit var repository: CommitCommentRepository

        @Before
        fun setup() {
            `when`(env.getGitLabAPIEndPoint()).thenReturn("http://localhost/api/v4/project/1")
            `when`(env.getGitLabToken()).thenReturn("token")
        }

        @Test
        fun 送信成功の場合() {
            // prepare
            // - CommitComment
            val commitComment = PowerMockito.mock(CommitComment::class.java)
            PowerMockito.`when`(commitComment.commitHash).thenReturn("commit-hash")
            PowerMockito.`when`(commitComment.getIndividualMessage()).thenReturn("individual-message\nindividual-message")
            PowerMockito.`when`(commitComment.filePath).thenReturn("file-path")
            PowerMockito.`when`(commitComment.lineNo).thenReturn(1234)
            // - RequestHelper
            `when`(helper.execute(any())).thenReturn(load("commit-comment-01.json"))

            // run
            repository.postIndividualComment(commitComment)

            // verify
            val captor = argumentCaptor<Request>()
            verify(helper, times(1)).execute(captor.capture())
            val request = captor.lastValue
            assertThat(request.url().toString(),
                    `is`("http://localhost/api/v4/project/1/repository/commits/commit-hash/comments"))
            assertThat(request.header("PRIVATE-TOKEN"), `is`("token"))
            val body = request.body()
            if (body is FormBody) {
                assertBody(body, "note", "individual-message\nindividual-message")
                assertBody(body, "path", "file-path")
                assertBody(body, "line", "1234")
                assertBody(body, "line_type", "new")
            } else {
                fail()
            }
        }

        private fun assertBody(body: FormBody, key: String, expected: String) {
            var index: Int? = null
            for (i in 0 until body.size()) {
                if (body.name(i) == key) {
                    index = i
                    break
                }
            }

            index ?: fail("key [$key] is not defined.")
            assertThat(body.value(index!!), `is`(expected))
        }

        private fun load(fileName: String): String {
            return File(javaClass.getResource(fileName).file).readText()
        }
    }

    @RunWith(PowerMockRunner::class)
    @PrepareForTest(CommitCommentRepository::class, Commit::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    class postSummaryComment {

        @Mock
        private lateinit var env: Env

        @Mock
        private lateinit var helper: RequestHelper

        @InjectMocks
        private lateinit var repository: CommitCommentRepository

        @Before
        fun setup() {
            `when`(env.getGitLabAPIEndPoint()).thenReturn("http://localhost/api/v4/project/1")
            `when`(env.getGitLabToken()).thenReturn("token")
        }

        @Test
        fun サマリーメッセージが存在する場合() {
            // prepare
            // - Commit
            val commit = PowerMockito.mock(Commit::class.java)
            PowerMockito.`when`(commit.hash).thenReturn("commit-hash")
            PowerMockito.`when`(commit.getSummaryMessage()).thenReturn("summary-message\nsummary-message")
            // - RequestHelper
            `when`(helper.execute(any())).thenReturn(load("commit-comment-01.json"))

            // run
            repository.postSummaryComment(commit)

            // verify
            val captor = argumentCaptor<Request>()
            verify(helper, times(1)).execute(captor.capture())
            val request = captor.lastValue
            assertThat(request.url().toString(),
                    `is`("http://localhost/api/v4/project/1/repository/commits/commit-hash/comments"))
            assertThat(request.header("PRIVATE-TOKEN"), `is`("token"))
            val body = request.body()
            if (body is FormBody) {
                assertBody(body, "note", "summary-message\nsummary-message")
                assertBody(body, "path", "")
                assertBody(body, "line", "-1")
                assertBody(body, "line_type", "new")
            } else {
                fail()
            }
        }

        @Test
        fun サマリーメッセージが存在しない場合() {
            // prepare
            // - Commit
            val commit = PowerMockito.mock(Commit::class.java)
            PowerMockito.`when`(commit.hash).thenReturn("commit-hash")
            PowerMockito.`when`(commit.getSummaryMessage()).thenReturn(null)
            // - RequestHelper
            `when`(helper.execute(any())).thenReturn(load("commit-comment-01.json"))

            // run
            repository.postSummaryComment(commit)

            // verify
            verify(helper, times(0)).execute(any())
        }

        private fun assertBody(body: FormBody, key: String, expected: String) {
            var index: Int? = null
            for (i in 0 until body.size()) {
                if (body.name(i) == key) {
                    index = i
                    break
                }
            }

            index ?: fail("key [$key] is not defined.")
            assertThat(body.value(index!!), `is`(expected))
        }

        private fun load(fileName: String): String {
            return File(javaClass.getResource(fileName).file).readText()
        }
    }
}
