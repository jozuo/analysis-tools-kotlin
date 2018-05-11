package com.jozuo.kotlin.analysis.gitlab.model

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(Enclosed::class)
class DiffInfoTest {

    class getter {

        private lateinit var diffInfo: DiffInfo
        private lateinit var ranges: List<Range>

        @Before
        fun setup() {
            ranges = listOf(Range(11, 22))
            diffInfo = DiffInfoTestBuilder("file path", ranges).build()
        }

        @Test
        fun filePath() {
            assertThat(diffInfo.filePath, `is`("file path"))
            assertThat(diffInfo.ranges, `is`(ranges))
        }

    }

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class isModifiedLine {

        @Rule
        @JvmField
        final var collector = ErrorCollector()

        @Test
        fun 範囲内かの判定() {
            val diffInfo = DiffInfoTestBuilder("file path", listOf(Range(11, 22))).build()
            collector.checkThat(diffInfo.isModifiedLine(10), `is`(false))
            collector.checkThat(diffInfo.isModifiedLine(11), `is`(true))
            collector.checkThat(diffInfo.isModifiedLine(22), `is`(true))
            collector.checkThat(diffInfo.isModifiedLine(23), `is`(false))
        }
    }
}

class DiffInfoTestBuilder(
        override var filePath: String,
        override var ranges: List<Range>) : DiffInfoBuilder {

    override fun build() = DiffInfo(this)
}