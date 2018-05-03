package com.jozuo.kotlin.analysis.git.model

import com.jozuo.kotlin.analysis.Env
import com.jozuo.kotlin.analysis.git.repository.GitRepository
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

@RunWith(Enclosed::class)
class ChangeFileListBuilderImplTest {

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class build {

        @MockBean
        private lateinit var env: Env

        @MockBean
        private lateinit var repository: GitRepository

        @Autowired
        private lateinit var builder: ChangeFileListBuilder

        @Before
        fun setup() {
            `when`(env.getCommitHashBegin()).thenReturn("commit-hash-begin")
            `when`(env.getCommitHashEnd()).thenReturn("commit-hash-end")
        }

        @After
        fun tearDown() {
            reset(env)
            reset(repository)
        }

        @Test
        fun 削除ステータスのコミットが含まれる場合() {
            // - 削除ステータスのファイルが削除されるはず

            // prepare
            val commits = listOf("1st-commit-hash")
            `when`(repository.getCommits(anyString(), anyString())).thenReturn(commits)

            val files = listOf(
                    "A\tsrc/app/add.ts",
                    "M\tsrc/app/modify.ts",
                    "D\tsrc/app/delete.ts",
                    "R\tsrc/app/rename.ts",
                    "C\tsrc/app/copy.ts")
            `when`(repository.getDiffFiles(anyString(), anyString())).thenReturn(files)

            // run
            val result = builder.build()

            // test
            assertThat(result, `is`(not(nullValue())))
            assertThat(result.changeFiles.size, `is`(4))
            assertChangeFile(result.changeFiles[0], "1st-commit-hash", "src/app/add.ts", "A")
            assertChangeFile(result.changeFiles[1], "1st-commit-hash", "src/app/modify.ts", "M")
            assertChangeFile(result.changeFiles[2], "1st-commit-hash", "src/app/rename.ts", "R")
            assertChangeFile(result.changeFiles[3], "1st-commit-hash", "src/app/copy.ts", "C")
            verify(repository, times(1)).getCommits("commit-hash-begin", "commit-hash-end")
            verify(repository, times(1)).getDiffFiles("commit-hash-begin", "1st-commit-hash")
        }

        @Test
        fun 複数コミットに同一ファイルがある場合() {
            // - 新しいコミットに紐付くファイルのみ残ること

            // prepare
            `when`(env.getCommitHashBegin()).thenReturn("commit-hash-begin")
            `when`(env.getCommitHashEnd()).thenReturn("commit-hash-end")

            val commits = listOf("3rd-commit-hash", "2nd-commit-hash", "1st-commit-hash")
            `when`(repository.getCommits(anyString(), anyString())).thenReturn(commits)
            `when`(repository.getDiffFiles(anyString(), anyString()))
                    .thenReturn(listOf("A\tsrc/app/hoge.ts", "M\tsrc/app/page.ts"))
                    .thenReturn(listOf("A\tsrc/app/page.ts", "M\tsrc/app/foo.ts"))
                    .thenReturn(listOf("A\tsrc/app/foo.ts", "M\tsrc/app/bar.ts"))

            // run
            val result = builder.build()

            // test
            assertThat(result, `is`(not(nullValue())))
            assertThat(result.changeFiles.size, `is`(4))
            assertChangeFile(result.changeFiles[0], "3rd-commit-hash", "src/app/hoge.ts", "A")
            assertChangeFile(result.changeFiles[1], "3rd-commit-hash", "src/app/page.ts", "M")
            assertChangeFile(result.changeFiles[2], "2nd-commit-hash", "src/app/foo.ts", "M")
            assertChangeFile(result.changeFiles[3], "1st-commit-hash", "src/app/bar.ts", "M")
            verify(repository, times(1)).getCommits("commit-hash-begin", "commit-hash-end")
            val order = inOrder(repository)
            order.verify(repository, times(1)).getDiffFiles("2nd-commit-hash", "3rd-commit-hash")
            order.verify(repository, times(1)).getDiffFiles("1st-commit-hash", "2nd-commit-hash")
            order.verify(repository, times(1)).getDiffFiles("commit-hash-begin", "1st-commit-hash")
        }

        @Test
        fun 差分ファイルが存在しない場合() {
            // - 空のリストになること

            // prepare
            `when`(env.getCommitHashBegin()).thenReturn("commit-hash-begin")
            `when`(env.getCommitHashEnd()).thenReturn("commit-hash-end")

            val commits = listOf("3rd-commit-hash", "2nd-commit-hash", "1st-commit-hash")
            `when`(repository.getCommits(anyString(), anyString())).thenReturn(commits)
            `when`(repository.getDiffFiles(anyString(), anyString()))
                    .thenReturn(listOf())
                    .thenReturn(listOf())
                    .thenReturn(listOf())

            // run
            val result = builder.build()

            // test
            assertThat(result, `is`(not(nullValue())))
            assertThat(result.changeFiles.size, `is`(0))
            verify(repository, times(1)).getCommits("commit-hash-begin", "commit-hash-end")
            val order = inOrder(repository)
            order.verify(repository, times(1)).getDiffFiles("2nd-commit-hash", "3rd-commit-hash")
            order.verify(repository, times(1)).getDiffFiles("1st-commit-hash", "2nd-commit-hash")
            order.verify(repository, times(1)).getDiffFiles("commit-hash-begin", "1st-commit-hash")
        }

        private fun assertChangeFile(file: ChangeFile, commitHash: String, name: String, status: String) {
            assertThat(file.commitHash, `is`(commitHash))
            assertThat(file.name, `is`(name))
            assertThat(file.status, `is`(status))
        }
    }
}