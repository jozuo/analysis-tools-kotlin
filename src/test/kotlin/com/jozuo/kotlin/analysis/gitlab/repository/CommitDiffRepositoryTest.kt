package com.jozuo.kotlin.analysis.gitlab.repository

import com.jozuo.kotlin.analysis.Env
import com.jozuo.kotlin.analysis.helper.RequestHelper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import okhttp3.Request
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.test.context.junit4.SpringRunner
import java.io.File

@RunWith(Enclosed::class)
class CommitDiffRepositoryTest {

    @RunWith(SpringRunner::class)
    class getDiffInfoList {

        @Mock
        private lateinit var env: Env

        @Mock
        private lateinit var helper: RequestHelper

        @InjectMocks
        private lateinit var repository: CommitDiffRepository

        @Before
        fun setup() {
            `when`(env.getGitLabAPIEndPoint()).thenReturn("http://localhost/api/vr/project/1")
            `when`(env.getGitLabToken()).thenReturn("token")
        }

        @Test
        fun リクエストが成功した場合() {
            `when`(helper.execute(any())).thenReturn(load("commit-diff-01.json"))

            // run
            repository.getDiffInfoList("commit-hash")

            // test
            val captor = argumentCaptor<Request>()
            verify(helper).execute(captor.capture())
            val response = captor.lastValue
            assertThat(response.url().toString(), `is`("http://localhost/api/vr/project/1/repository/commits/commit-hash/diff"))
            assertThat(response.header("PRIVATE-TOKEN"), `is`("token"))
            assertThat(response.body(), `is`(nullValue()))
        }

        private fun load(fileName: String): String {
            return File(javaClass.getResource(fileName).file).readText()
        }
    }
}