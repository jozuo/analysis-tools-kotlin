package com.jozuo.kotlin.analysis.helper

import com.google.gson.reflect.TypeToken
import okhttp3.Request
import org.hamcrest.CoreMatchers.*
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

        val baseUrl = "http://localhost/api/v4"

        @Test
        fun レスポンスがオブジェクトの場合() {
            // run
            val request = Request.Builder()
                    .url("$baseUrl/projects/1")
                    .header("PRIVATE-TOKEN", "t3zTVztoEz2HJ5Rs6LEX")
                    .get()
                    .build()
            val project = helper.execute(request, Project::class.java)

            // test
            assertThat(project.id, `is`(1))
            assertThat(project.defaultBranch, `is`("master"))
            assertThat(project.archived, `is`(false))
            assertThat(project.groupAccess, `is`(nullValue()))
        }

        @Test
        fun レスポンスが配列の場合() {
            // run
            val request = Request.Builder()
                    .url("$baseUrl/projects/1/repository/commits/f962449f47d2c034db3a922f6ce6b94bc5017c9c/diff")
                    .header("PRIVATE-TOKEN", "t3zTVztoEz2HJ5Rs6LEX")
                    .get()
                    .build()

            val type = object : TypeToken<List<Diff>>() {}.type
            val diffs = helper.execute<List<Diff>>(request, type)
            assertThat(diffs.size, `is`(1))
            assertThat(diffs[0].diff, `is`(""))
            assertThat(diffs[0].newPath, `is`("sonar-project.properties"))
            assertThat(diffs[0].newFile, `is`(false))
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
                helper.execute(request, Project::class.java)
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

class Project {
    var id: Int? = null
    var defaultBranch: String? = null
    var archived: Boolean? = null
    var groupAccess: String? = null
}

class Diff {
    val diff: String? = null
    val newPath: String? = null
    val newFile: Boolean? = null
}
