package com.jozuo.kotlin.analysis.git.repository

import com.jozuo.kotlin.analysis.Env
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit4.SpringRunner

@RunWith(Enclosed::class)
// 実行環境に依存したテストのため通常は実行しない
@Ignore
class GitRepositoryTest {

    @RunWith(SpringRunner::class)
    class getDiffFiles {

        @Mock
        private lateinit var env: Env

        @InjectMocks
        private lateinit var repository: GitRepository

        @Rule
        @JvmField
        val thrown = ExpectedException.none()!!

        @Test
        fun 差分が取得できる場合() {
            // prepare
            `when`(env.getProjectRoot()).thenReturn("/Users/toru/work/professional-tool")

            // test
            val results = repository.getDiffFiles("007ab8", "cc75e5")
            assertThat(results.size, `is`(4))
            assertThat(results[0], `is`("A\tDockerfile"))
            assertThat(results[1], `is`("M\tapp/src/component/area-correction/area-correction.component.ts"))
            assertThat(results[2], `is`("M\tpackage.json"))
            assertThat(results[3], `is`("A\ttslint.json"))
        }

        @Test
        fun 差分が取得できない場合() {
            // prepare
            `when`(env.getProjectRoot()).thenReturn("/Users/toru/work/professional-tool")

            val results = repository.getDiffFiles("cc75e5", "cc75e5")
            assertThat(results.size, `is`(0))
        }

        @Test
        fun コマンド実行に失敗する場合() {
            // prepare
            `when`(env.getProjectRoot()).thenReturn("/Users/toru/work")
            thrown.expect(IllegalStateException::class.java)

            val results = repository.getDiffFiles("cc75e5", "cc75e5")
            assertThat(results.size, `is`(0))
        }
    }

    @RunWith(SpringRunner::class)
    class getCommits {

        @Mock
       private  lateinit var env: Env

        @InjectMocks
        private lateinit var repository: GitRepository

        @Rule
        @JvmField
        val thrown = ExpectedException.none()!!

        // 実行環境に依存したテストのため通常は実行しない
        @Test
        fun 差分が取得できる場合() {
            // prepare
            `when`(env.getProjectRoot()).thenReturn("/Users/toru/work/professional-tool")

            // test
            val results = repository.getCommits("007ab8", "cc75e5")
            assertThat(results.size, `is`(6))
            assertThat(results[0], `is`("cc75e5680d63254eeca922185cc09106978ffe96"))
            assertThat(results[1], `is`("f0558560c4555e3132e530ed7e2bd7ab7dff366b"))
            assertThat(results[2], `is`("8715bfe444bdb786b4014c6f59c651208a82ffba"))
            assertThat(results[3], `is`("5d6e360f28001a8ecf1a6e0c29b3f253f7f260a0"))
            assertThat(results[4], `is`("f68112cdd3566334e7ee8990c30aa43f96d7815f"))
            assertThat(results[5], `is`("46416df85b19c4fde230d56b2865e83582cf0446"))
        }

        // 実行環境に依存したテストのため通常は実行しない
        @Test
        fun 差分が取得できない場合() {
            // prepare
            `when`(env.getProjectRoot()).thenReturn("/Users/toru/work/professional-tool")

            val results = repository.getCommits("cc75e5", "cc75e5")
            assertThat(results.size, `is`(0))
        }

        // 実行環境に依存したテストのため通常は実行しない
        @Test
        fun コマンド実行に失敗する場合() {
            // prepare
            `when`(env.getProjectRoot()).thenReturn("/Users/toru/work")
            thrown.expect(IllegalStateException::class.java)

            val results = repository.getCommits("cc75e5", "cc75e5")
            assertThat(results.size, `is`(0))
        }
    }
}