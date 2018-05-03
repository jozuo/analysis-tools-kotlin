package com.jozuo.kotlin.analysis.git.command

import com.jozuo.kotlin.analysis.git.service.DiffFileService
import com.nhaarman.mockito_kotlin.doNothing
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import java.io.ByteArrayOutputStream
import java.io.PrintStream

@RunWith(Enclosed::class)
class DiffFileCommandTest {

    @RunWith(SpringRunner::class)
    @SpringBootTest
    @TestPropertySource(properties = ["command=diff-file"])
    class execute {

        @MockBean
        private lateinit var service: DiffFileService

        @Autowired
        private lateinit var command: DiffFileCommand

        private lateinit var out: ByteArrayOutputStream

        @Before
        fun setup() {
            out = ByteArrayOutputStream()
            System.setOut(PrintStream(out))
        }

        @Test
        fun 必須オプションが指定されている場合() {
            // prepare
            doNothing().`when`(service).execute(anyString())

            // run
            val args = arrayOf("-o", "dest-dir")
            assertThat(command.execute(args), `is`(true))

            // test
            verify(service, times(1)).execute("dest-dir")
            assertThat(out.toString(), `is`(""))
        }

        @Test
        fun ヘルプオプションが指定されている場合() {
            // prepare
            doNothing().`when`(service).execute(anyString())

            // run
            val args = arrayOf("-o", "dest-dir", "-h")
            assertThat(command.execute(args), `is`(false))

            // test
            verify(service, never()).execute(anyString())
            assertThat(out.toString(), containsString("usage: java -jar analysis.jar --command=diff-file [-o <arg>]"))
        }

        @Test
        fun 必須オプションが指定されていない場合() {
            // prepare
            doNothing().`when`(service).execute(anyString())

            // run
            val args = arrayOf("")
            assertThat(command.execute(args), `is`(false))

            // test
            verify(service, never()).execute(anyString())
            assertThat(out.toString(), containsString("usage: java -jar analysis.jar --command=diff-file [-o <arg>]"))
        }

        @Test
        fun 関係の無いオプションが指定されている場合() {
            // prepare
            doNothing().`when`(service).execute(anyString())

            // run
            val args = arrayOf("-o", "dest-dir", "-p", "param")
            assertThat(command.execute(args), `is`(false))

            // test
            verify(service, never()).execute(anyString())
            assertThat(out.toString(), containsString("Unrecognized option: -p"))
            assertThat(out.toString(), containsString("usage: java -jar analysis.jar --command=diff-file [-o <arg>]"))
        }
    }
}