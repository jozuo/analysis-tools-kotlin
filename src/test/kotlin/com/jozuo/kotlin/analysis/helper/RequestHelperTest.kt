package com.jozuo.kotlin.analysis.helper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Request
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.fail
import org.junit.Ignore
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.io.ByteArrayOutputStream
import java.io.PrintStream

@RunWith(Enclosed::class)
// 実行環境に依存したテストのため通常は実行しない
@Ignore
class RequestHelperTest {

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class executeGET {

        @Autowired
        private lateinit var helper: RequestHelper


        private val type = object : TypeToken<Map<String, Any>>() {}.type

        val baseUrl = "http://localhost/api/v4"

        @Test
        fun リクエスト成功の場合() {
            // run
            val request = Request.Builder()
                    .url("$baseUrl/projects/1")
                    .header("PRIVATE-TOKEN", "t3zTVztoEz2HJ5Rs6LEX")
                    .get()
                    .build()
            val responseBody = helper.execute(request)

            // test
            val responseMap = Gson().fromJson<Map<String, Any>>(responseBody, type)
            assertThat(responseMap["id"].toString(), `is`("1.0"))
            assertThat(responseMap["default_branch"].toString(), `is`("master"))
            assertThat(responseMap["archived"].toString(), `is`("false"))
        }

        @Test
        fun 例外レスポンスの場合() {
            // prepare
            val original = System.out
            val out = ByteArrayOutputStream()
            System.setOut(PrintStream(out))

            // run
            val request = Request.Builder()
                    .url("$baseUrl/projects/2")
                    .header("PRIVATE-TOKEN", "t3zTVztoEz2HJ5Rs6LEX")
                    .get()
                    .build()

            try {
                helper.execute(request)
                fail()
            } catch (e: IllegalStateException) {
                // assert
                assertThat(e.message, `is`("GET operation failed."))
                assertThat(out.toString(), containsString("code: 404"))
                assertThat(out.toString(), containsString("body: {\"message\":\"404 Project Not Found\"}"))
            } finally {
                System.setOut(original)
            }
        }
    }
}
