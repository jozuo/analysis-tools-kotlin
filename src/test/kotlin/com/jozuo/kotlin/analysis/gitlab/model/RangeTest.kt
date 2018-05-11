package com.jozuo.kotlin.analysis.gitlab.model

import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(Enclosed::class)
class RangeTest {

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class isModifiedLine {

        @Rule
        @JvmField
        final var collector = ErrorCollector()

        private lateinit var range: Range

        @Before
        fun setup() {
            range = Range(10, 20)
        }

        @Test
        fun 範囲判定() {
            collector.checkThat(range.isInside(9), `is`(false))
            collector.checkThat(range.isInside(10), `is`(true))
            collector.checkThat(range.isInside(20), `is`(true))
            collector.checkThat(range.isInside(21), `is`(false))
        }
    }
}