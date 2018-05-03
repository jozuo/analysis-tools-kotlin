package com.jozuo.kotlin.analysis.sample

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class ChildTest {

    @Autowired
    private lateinit var child: Child

    @Test
    fun test01() {
        println(child.get("hogehoge"))
    }
}