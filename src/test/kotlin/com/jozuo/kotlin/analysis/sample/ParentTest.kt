package com.jozuo.kotlin.analysis.sample

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.Matchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner


@RunWith(Enclosed::class)
class ParentTest {

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class get {
        private lateinit var parent: Parent

        @MockBean
        private lateinit var child: Child

        @Autowired
        private lateinit var beanFactory: AutowireCapableBeanFactory

        @Before
        fun setup() {
            parent = Parent()
            beanFactory.autowireBean(parent)
        }

        @Test
        fun test01() {
            // prepare
            `when`(child.get(anyString())).thenReturn("Hello World")

            // test
            assertThat(parent.get(), `is`("Hello World"))
//
//            verify(child, times(1)).get("hogehoge")
//            val captor = argumentCaptor<String>()
//            verify(child, times(1)).get(captor.capture())
//            assertThat(captor.lastValue, `is`("hogehoge"))
        }
    }
}