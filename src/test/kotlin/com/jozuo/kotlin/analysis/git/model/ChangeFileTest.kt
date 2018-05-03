package com.jozuo.kotlin.analysis.git.model

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
class ChangeFileTest {

    class getter {
        private lateinit var instance: ChangeFile

        @Before
        fun setup() {
            instance = ChangeFile("commit-hash", "name", "status")
        }

        @Test
        fun getCommitHash() {
            assertThat(instance.commitHash, `is`("commit-hash"))
        }

        @Test
        fun getName() {
            assertThat(instance.name, `is`("name"))
        }

        @Test
        fun getStatus() {
            assertThat(instance.status, `is`("status"))
        }
        @Test
        fun equals() {
            val obj1 = ChangeFile("hoge", "page", "foo")
            val obj2 = ChangeFile("hoge", "page", "foo")
            assertThat((obj1 == obj2), `is`(true))
        }
    }
}