package com.jozuo.kotlin.analysis.gitlab.model

import com.jozuo.kotlin.analysis.gitlab.repository.CommitDiffRepository
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(Enclosed::class)
class DiffInfoBuilderImplTest {

    @RunWith(SpringRunner::class)
    @SpringBootTest
    class build {

        private lateinit var builder: DiffInfoBuilderImpl
        private lateinit var entity: CommitDiffRepository.Entity

        @Before
        fun setup() {
            builder = DiffInfoBuilderImpl()
        }

        @Test
        fun 範囲情報が存在しない場合() {
            // prepare
            entity = CommitDiffRepository.Entity()
            entity.deletedFile = false
            entity.diff = ""
            entity.newPath = "sonar-project.properties"

            // run
            builder.setEntity(entity)
            val result = builder.build()

            // test
            assertThat(result.filePath, `is`("sonar-project.properties"))
            assertThat(result.ranges.size, `is`(0))
        }

        @Test
        fun 行番号が1桁の場合() {
            // prepare
            entity = CommitDiffRepository.Entity()
            entity.deletedFile = false
            entity.diff = "--- a/app/hoge.ts\n+++b/app/hoge.ts\n@@ -1,3 +2,3 @@ hoge hoge\n@@ -4,3 +5,3 @@ page page"
            entity.newPath = "sonar-project.properties"


            // run
            builder.setEntity(entity)
            val result = builder.build()

            // test
            assertThat(result.filePath, `is`("sonar-project.properties"))
            assertThat(result.ranges.size, `is`(2))
            assertThat(result.ranges[0].toString(), `is`("Range(begin=2, end=5)"))
            assertThat(result.ranges[1].toString(), `is`("Range(begin=5, end=8)"))
        }

        @Test
        fun 行番号が2桁の場合() {
            // prepare
            entity = CommitDiffRepository.Entity()
            entity.deletedFile = false
            entity.diff = "--- a/app/hoge.ts\n+++b/app/hoge.ts\n@@ -11,13 +12,13 @@ hoge\n@@ -44,13 +45,13 @@ page"
            entity.newPath = "sonar-project.properties"


            // run
            builder.setEntity(entity)
            val result = builder.build()

            // test
            assertThat(result.filePath, `is`("sonar-project.properties"))
            assertThat(result.ranges.size, `is`(2))
            assertThat(result.ranges[0].toString(), `is`("Range(begin=12, end=25)"))
            assertThat(result.ranges[1].toString(), `is`("Range(begin=45, end=58)"))
        }

        @Test
        fun 行番号が3桁の場合() {
            // prepare
            entity = CommitDiffRepository.Entity()
            entity.deletedFile = false
            entity.diff = "--- a/app/hoge.ts\n+++b/app/hoge.ts\n@@ -111,113 +112,113 @@ hoge\n@@ -244,213 +245,213 @@ page"
            entity.newPath = "sonar-project.properties"


            // run
            builder.setEntity(entity)
            val result = builder.build()

            // test
            assertThat(result.filePath, `is`("sonar-project.properties"))
            assertThat(result.ranges.size, `is`(2))
            assertThat(result.ranges[0].toString(), `is`("Range(begin=112, end=225)"))
            assertThat(result.ranges[1].toString(), `is`("Range(begin=245, end=458)"))
        }

        @Test
        fun 変更箇所が3カ所の場合() {
            // prepare
            entity = CommitDiffRepository.Entity()
            entity.deletedFile = false
            entity.diff = "--- a/hoge.ts\n+++b/hoge.ts\n@@ -1,1 +2,2 @@ hoge\n@@ -5,2 +6,2 @@ page\n@@ -10,12 +11,12 @@ oo"
            entity.newPath = "sonar-project.properties"


            // run
            builder.setEntity(entity)
            val result = builder.build()

            // test
            assertThat(result.filePath, `is`("sonar-project.properties"))
            assertThat(result.ranges.size, `is`(3))
            assertThat(result.ranges[0].toString(), `is`("Range(begin=2, end=4)"))
            assertThat(result.ranges[1].toString(), `is`("Range(begin=6, end=8)"))
            assertThat(result.ranges[2].toString(), `is`("Range(begin=11, end=23)"))
        }
    }
}