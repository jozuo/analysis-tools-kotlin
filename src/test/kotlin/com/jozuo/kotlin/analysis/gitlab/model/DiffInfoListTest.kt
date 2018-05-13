package com.jozuo.kotlin.analysis.gitlab.model

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.Matchers.anyInt
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(Enclosed::class)
class DiffInfoListTest {

    @RunWith(PowerMockRunner::class)
    @PrepareForTest(DiffInfoList::class, DiffInfo::class)
    class isModifiedLine {

        @Test
        fun 変更行の場合() {
            // prepare
            val diffInfo1 = mock(DiffInfo::class.java)
            `when`(diffInfo1.isModifiedLine(anyInt())).thenReturn(false)
            `when`(diffInfo1.filePath).thenReturn("hoge.ts")

            val diffInfo2 = mock(DiffInfo::class.java)
            `when`(diffInfo2.isModifiedLine(anyInt())).thenReturn(true)
            `when`(diffInfo2.filePath).thenReturn("page.ts")

            // test
            val diffInfoList = DiffInfoList(listOf(diffInfo1, diffInfo2))
            assertThat(diffInfoList.isModifiedLine("page.ts", 10), `is`(true))
            verify(diffInfo1, times(0)).isModifiedLine(anyInt())
            verify(diffInfo2, times(1)).isModifiedLine(10)
        }

        @Test
        fun ファイル名が存在しない場合() {
            // prepare
            val diffInfo1 = mock(DiffInfo::class.java)
            `when`(diffInfo1.isModifiedLine(10)).thenReturn(true)
            `when`(diffInfo1.filePath).thenReturn("hoge.ts")

            val diffInfo2 = mock(DiffInfo::class.java)
            `when`(diffInfo2.isModifiedLine(10)).thenReturn(true)
            `when`(diffInfo2.filePath).thenReturn("page.ts")

            // test
            val diffInfoList = DiffInfoList(listOf(diffInfo1, diffInfo2))
            assertThat(diffInfoList.isModifiedLine("foo.ts", 10), `is`(false))
            verify(diffInfo1, times(0)).isModifiedLine(anyInt())
            verify(diffInfo2, times(0)).isModifiedLine(anyInt())
        }

        @Test
        fun ファイル名は存在するが行番号が異なる場合() {
            // prepare
            val diffInfo1 = mock(DiffInfo::class.java)
            `when`(diffInfo1.isModifiedLine(10)).thenReturn(true)
            `when`(diffInfo1.filePath).thenReturn("hoge.ts")

            val diffInfo2 = mock(DiffInfo::class.java)
            `when`(diffInfo2.isModifiedLine(10)).thenReturn(false)
            `when`(diffInfo2.filePath).thenReturn("page.ts")

            // test
            val diffInfoList = DiffInfoList(listOf(diffInfo1, diffInfo2))
            assertThat(diffInfoList.isModifiedLine("page.ts", 10), `is`(false))
            verify(diffInfo1, times(0)).isModifiedLine(anyInt())
            verify(diffInfo2, times(1)).isModifiedLine(10)
        }
    }
}