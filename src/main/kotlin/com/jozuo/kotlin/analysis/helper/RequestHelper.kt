package com.jozuo.kotlin.analysis.helper

import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.lang3.builder.ReflectionToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.springframework.stereotype.Component

@Component
class RequestHelper {

    // TODO Proxyの考慮
    private val client: OkHttpClient = OkHttpClient()

    fun execute(request: Request): String {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            return response.body()?.string() ?: ""
        }

        println("${request.method()} operation failed.")
        println("--- request ---")
        println(ReflectionToStringBuilder.toString(request, ToStringStyle.JSON_STYLE))
        println("--- response ---")
        println("code: ${response.code()}")
        println("body: ${response.body()?.string()}")
        throw IllegalStateException("${request.method()} operation failed.")
    }
}