package com.jozuo.kotlin.analysis.sample

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.springframework.test.context.junit4.SpringRunner

@RunWith(Enclosed::class)
class HogeTest {

    @RunWith(PowerMockRunner::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    @PrepareForTest(System::class, Hoge::class)
    class getEnv {
        @InjectMocks
        private lateinit var hoge: Hoge

        @Rule
        @JvmField
        val thrown = ExpectedException.none()!!

        @Before
        fun setup() {
            mockStatic(System::class.java)
        }

        @Test
        fun success() {
            // prepare
            `when`(System.getenv("hoge")).thenReturn("page")
            // test
            assertThat(hoge.getEnv("hoge"), `is`("page"))
        }

        @Test
        fun failure() {
            // prepare
            `when`(System.getenv("hoge")).thenReturn(null)
            thrown.expect(IllegalStateException::class.java)
            thrown.expectMessage("hoge is not defined.")
            // run
            hoge.getEnv("hoge")
        }
    }
}
