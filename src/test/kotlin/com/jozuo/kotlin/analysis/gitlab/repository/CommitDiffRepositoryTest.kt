package com.jozuo.kotlin.analysis.gitlab.repository

import com.jozuo.kotlin.analysis.Env
import com.jozuo.kotlin.analysis.gitlab.model.DiffInfo
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
import org.mockito.Mockito.*
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
            `when`(env.getGitLabAPIEndPoint()).thenReturn("http://localhost/api/v4/project/1")
            `when`(env.getGitLabToken()).thenReturn("token")
        }

        @Test
        fun 正常レスポンスの場合() {
            `when`(helper.execute(any())).thenReturn(load("commit-diff-01.json"))

            // run
            val diffInfoList = repository.getDiffInfoList("commit-hash")

            // test
            var diff: DiffInfo

            assertThat(diffInfoList.diffInfos.size, `is`(3))
            // - 1ファイル名
            diff = diffInfoList.diffInfos[0]
            assertThat(diff.filePath, `is`("app/src/component/area-correction/area-correction.component.ts"))
            assertThat(diff.ranges.size, `is`(2))
            assertThat(diff.ranges[0].toString(), `is`("Range(begin=2, end=11)"))
            assertThat(diff.ranges[1].toString(), `is`("Range(begin=36, end=44)"))
            // 2ファイル名
            diff = diffInfoList.diffInfos[1]
            assertThat(diff.filePath, `is`("app/src/component/color-matching/color-matching.component.ts"))
            assertThat(diff.ranges.size, `is`(2))
            assertThat(diff.ranges[0].toString(), `is`("Range(begin=15, end=22)"))
            assertThat(diff.ranges[1].toString(), `is`("Range(begin=87, end=105)"))
            // 3ファイル名
            diff = diffInfoList.diffInfos[2]
            assertThat(diff.filePath, `is`("app/src/component/geometry-off/geometry-off.component.ts"))
            assertThat(diff.ranges.size, `is`(2))
            assertThat(diff.ranges[0].toString(), `is`("Range(begin=1, end=8)"))
            assertThat(diff.ranges[1].toString(), `is`("Range(begin=25, end=152)"))

            val captor = argumentCaptor<Request>()
            verify(helper).execute(captor.capture())
            val request = captor.lastValue
            assertThat(request.url().toString(), `is`("http://localhost/api/v4/project/1/repository/commits/commit-hash/diff"))
            assertThat(request.header("PRIVATE-TOKEN"), `is`("token"))
            assertThat(request.body(), `is`(nullValue()))
        }

        private fun load(fileName: String): String {
            return File(javaClass.getResource(fileName).file).readText()
        }
    }
}