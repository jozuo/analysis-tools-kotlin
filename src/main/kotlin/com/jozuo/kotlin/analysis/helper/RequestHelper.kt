package com.jozuo.kotlin.analysis.helper

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.lang3.builder.ReflectionToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.springframework.stereotype.Component
import java.lang.reflect.Type

@Component
class RequestHelper {

    // TODO Proxyの考慮
    private val client: OkHttpClient = OkHttpClient()

    private val gson: Gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    fun <T> execute(request: Request, clazz: Class<T>): T {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            return gson.fromJson<T>(response.body()?.string(), clazz)
        }
        throw handleErrorResponse(request, response)
    }

    fun <T> execute(request: Request, type: Type): T {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            return gson.fromJson<T>(response.body()?.string(), type)
        }
        throw handleErrorResponse(request, response)
    }

    private fun handleErrorResponse(request: Request, response: Response): Exception {
        println("${request.method()} operation failed.")
        println("--- request ---")
        println(ReflectionToStringBuilder.toString(request, ToStringStyle.JSON_STYLE))
        println("--- response ---")
        println("code: ${response.code()}")
        println("body: ${response.body()?.string()}")
        return IllegalStateException("${request.method()} operation failed.")
    }
}