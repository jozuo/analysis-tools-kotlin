package com.jozuo.kotlin.analysis

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.ErrorCollector
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.springframework.test.context.junit4.SpringRunner


@RunWith(Enclosed::class)
class EnvTest {

    @RunWith(PowerMockRunner::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    @PrepareForTest(System::class, Env::class)
    class getProjectRoot {

        private val envKey = "PROJECT_ROOT"

        @InjectMocks
        private lateinit var env: Env
        @Rule
        @JvmField
        val thrown = ExpectedException.none()!!

        @Before
        fun setup() {
            PowerMockito.mockStatic(System::class.java)
        }

        @Test
        fun 環境変数が設定されている場合() {
            // prepare
            `when`(System.getenv(envKey)).thenReturn("/home/user/workspace")

            // test
            assertThat(env.getProjectRoot(), `is`("/home/user/workspace"))
        }

        @Test
        fun 環境変数が設定されていない場合() {
            // prepare
            `when`(System.getenv(envKey)).thenReturn(null)
            thrown.expect(IllegalStateException::class.java)
            thrown.expectMessage("PROJECT_ROOT is not defined.")

            // run
            env.getProjectRoot()
        }
    }

    @RunWith(PowerMockRunner::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    @PrepareForTest(System::class, Env::class)
    class getCommitHashBegin {

        private val envKey = "COMMIT_HASH_BEGIN"

        @InjectMocks
        private lateinit var env: Env

        @Rule
        @JvmField
        val thrown = ExpectedException.none()!!

        @Before
        fun setup() {
            PowerMockito.mockStatic(System::class.java)
        }

        @Test
        fun 環境変数が設定されている場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn("commit hash begin")

            // test
            assertThat(env.getCommitHashBegin(), `is`("commit hash begin"))
        }

        @Test
        fun 環境変数が設定されていない場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn(null)
            thrown.expect(IllegalStateException::class.java)
            thrown.expectMessage("COMMIT_HASH_BEGIN is not defined.")

            // run
            env.getCommitHashBegin()
        }
    }


    @RunWith(PowerMockRunner::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    @PrepareForTest(System::class, Env::class)
    class getCommitHashEnd {

        private val envKey = "COMMIT_HASH_END"

        @InjectMocks
        private lateinit var env: Env

        @Rule
        @JvmField
        val thrown = ExpectedException.none()!!

        @Before
        fun setup() {
            PowerMockito.mockStatic(System::class.java)
        }

        @Test
        fun 環境変数が設定されている場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn("commit hash end")

            // test
            assertThat(env.getCommitHashEnd(), `is`("commit hash end"))
        }

        @Test
        fun 環境変数が設定されていない場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn(null)
            thrown.expect(IllegalStateException::class.java)
            thrown.expectMessage("COMMIT_HASH_END is not defined.")

            // run
            env.getCommitHashEnd()
        }
    }


    @RunWith(PowerMockRunner::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    @PrepareForTest(System::class, Env::class)
    class getGitLabUrl {

        private val envKey = "GITLAB_URL"

        @InjectMocks
        private lateinit var env: Env

        @Rule
        @JvmField
        val thrown = ExpectedException.none()!!

        @Before
        fun setup() {
            PowerMockito.mockStatic(System::class.java)
        }

        @Test
        fun 環境変数が設定されている場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn("http://localhost/user/project")

            // test
            assertThat(env.getGitLabUrl(), `is`("http://localhost/user/project"))
        }

        @Test
        fun 環境変数が設定されていない場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn(null)
            thrown.expect(IllegalStateException::class.java)
            thrown.expectMessage("GITLAB_URL is not defined.")

            // run
            env.getGitLabUrl()
        }
    }

    @RunWith(PowerMockRunner::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    @PrepareForTest(System::class, Env::class)
    class getGitLabAPIEndPoint {

        private val envKey1 = "GITLAB_URL"
        private val envKey2 = "GITLAB_PROJECT_ID"

        @InjectMocks
        private lateinit var env: Env

        @Rule
        @JvmField
        val thrown = ExpectedException.none()!!

        @Before
        fun setup() {
            PowerMockito.mockStatic(System::class.java)
        }

        @Test
        fun 必要な環境変数が全て設定されている場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey1)).thenReturn("http://localhost/user/project")
            PowerMockito.`when`(System.getenv(envKey2)).thenReturn("123")

            // test
            assertThat(env.getGitLabAPIEndPoint(), `is`("http://localhost/api/v4/projects/123"))
        }

        @Test
        fun 一部の環境変数が設定されていない場合1() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey1)).thenReturn(null)
            PowerMockito.`when`(System.getenv(envKey2)).thenReturn("123")
            thrown.expect(IllegalStateException::class.java)
            thrown.expectMessage("GITLAB_URL is not defined.")

            // run
            env.getGitLabAPIEndPoint()
        }

        @Test
        fun 一部の環境変数が設定されていない場合2() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey1)).thenReturn("http://localhost/user/project")
            PowerMockito.`when`(System.getenv(envKey2)).thenReturn(null)
            thrown.expect(IllegalStateException::class.java)
            thrown.expectMessage("GITLAB_PROJECT_ID is not defined.")

            // run
            env.getGitLabAPIEndPoint()
        }
    }


    @RunWith(PowerMockRunner::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    @PrepareForTest(System::class, Env::class)
    class getGitLabToken {

        private val envKey = "GITLAB_TOKEN"

        @InjectMocks
        private lateinit var env: Env

        @Rule
        @JvmField
        val thrown = ExpectedException.none()!!

        @Before
        fun setup() {
            PowerMockito.mockStatic(System::class.java)
        }

        @Test
        fun 環境変数が設定されている場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn("gitlab token")

            // test
            assertThat(env.getGitLabToken(), `is`("gitlab token"))
        }

        @Test
        fun 環境変数が設定されていない場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn(null)
            thrown.expect(IllegalStateException::class.java)
            thrown.expectMessage("GITLAB_TOKEN is not defined.")

            // run
            env.getGitLabToken()
        }
    }


    @RunWith(PowerMockRunner::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    @PrepareForTest(System::class, Env::class)
    class getGitLabBranch {

        private val envKey = "GITLAB_BRANCH"

        @InjectMocks
        private lateinit var env: Env

        @Rule
        @JvmField
        val thrown = ExpectedException.none()!!

        @Before
        fun setup() {
            PowerMockito.mockStatic(System::class.java)
        }

        @Test
        fun 環境変数が設定されている場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn("gitlab branch")

            // test
            assertThat(env.getGitLabBranch(), `is`("gitlab branch"))
        }

        @Test
        fun 環境変数が設定されていない場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn(null)
            thrown.expect(IllegalStateException::class.java)
            thrown.expectMessage("GITLAB_BRANCH is not defined.")

            // run
            env.getGitLabBranch()
        }
    }


    @RunWith(PowerMockRunner::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    @PrepareForTest(System::class, Env::class)
    class jenkinsBuildUrl {

        private val envKey = "BUILD_URL"

        @InjectMocks
        private lateinit var env: Env

        @Rule
        @JvmField
        val thrown = ExpectedException.none()!!

        @Before
        fun setup() {
            PowerMockito.mockStatic(System::class.java)
        }

        @Test
        fun 環境変数が設定されている場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn("http://jenkins/build/123")

            // test
            assertThat(env.getJenkinsBuildUrl(), `is`("http://jenkins/build/123"))
        }

        @Test
        fun 環境変数が設定されていない場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn(null)
            thrown.expect(IllegalStateException::class.java)
            thrown.expectMessage("BUILD_URL is not defined.")

            // run
            env.getJenkinsBuildUrl()
        }
    }


    @RunWith(PowerMockRunner::class)
    @PowerMockRunnerDelegate(SpringRunner::class)
    @PrepareForTest(System::class, Env::class)
    class isDebugEnable {

        private val envKey = "DEBUG"

        @InjectMocks
        private lateinit var env: Env

        @Rule
        @JvmField
        val collector = ErrorCollector()

        @Before
        fun setup() {
            PowerMockito.mockStatic(System::class.java)
        }

        @Test
        fun 環境変数が設定されていない場合() {
            // prepare
            PowerMockito.`when`(System.getenv(envKey)).thenReturn(null)

            // test
            assertThat(env.isDebugEnable(), `is`(false))
        }

        @Test
        fun ONを表す環境変数が設定されている場合() {
            PowerMockito.`when`(System.getenv(envKey)).thenReturn("1")
            collector.checkThat("no.1", env.isDebugEnable(), `is`(true))

            PowerMockito.`when`(System.getenv(envKey)).thenReturn("true")
            collector.checkThat("no.2", env.isDebugEnable(), `is`(true))

            PowerMockito.`when`(System.getenv(envKey)).thenReturn("TrUe")
            collector.checkThat("no.3", env.isDebugEnable(), `is`(true))

            PowerMockito.`when`(System.getenv(envKey)).thenReturn("on")
            collector.checkThat("no.4", env.isDebugEnable(), `is`(true))

            PowerMockito.`when`(System.getenv(envKey)).thenReturn("oN")
            collector.checkThat("no.5", env.isDebugEnable(), `is`(true))
        }

        @Test
        fun OFFを表す環境変数が設定されている場合() {
            PowerMockito.`when`(System.getenv(envKey)).thenReturn("-1")
            collector.checkThat("no.2", env.isDebugEnable(), `is`(false))

            PowerMockito.`when`(System.getenv(envKey)).thenReturn("off")
            collector.checkThat("no.3", env.isDebugEnable(), `is`(false))

            PowerMockito.`when`(System.getenv(envKey)).thenReturn("false")
            collector.checkThat("no.4", env.isDebugEnable(), `is`(false))

            PowerMockito.`when`(System.getenv(envKey)).thenReturn("hoge")
            collector.checkThat("no.4", env.isDebugEnable(), `is`(false))
        }
    }
}