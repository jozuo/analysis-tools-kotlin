package com.jozuo.kotlin.analysis.repository

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

abstract class ApiCallRepository {

    private val gson: Gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    protected fun <T> toResponse(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }

    protected fun <T> toResponse(json: String, clazz: Class<T>): T {
        return gson.fromJson(json, clazz)
    }
}