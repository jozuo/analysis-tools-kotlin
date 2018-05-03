package com.jozuo.kotlin.analysis

import org.springframework.stereotype.Service

@Service
class Env {
    fun getProjectRoot(): String {
        return getEnv("PROJECT_ROOT")
    }

    fun getCommitHashBegin(): String {
        return getEnv("COMMIT_HASH_BEGIN")
    }

    fun getCommitHashEnd(): String {
        return getEnv("COMMIT_HASH_END")
    }

    fun getGitLabUrl(): String {
        return getEnv("GITLAB_URL")
    }

    fun getGitLabAPIEndPoint(): String {
        var url = getGitLabUrl()
        val projectId = getEnv("GITLAB_PROJECT_ID")
        url = url.substring(0, url.lastIndexOf("/"))
        url = url.substring(0, url.lastIndexOf("/"))

        return "$url/api/v4/projects/$projectId"
    }

    fun getGitLabToken(): String {
        return getEnv("GITLAB_TOKEN")
    }

    fun getGitLabBranch(): String {
        return getEnv("GITLAB_BRANCH")
    }

    fun getJenkinsBuildUrl(): String {
        return getEnv("BUILD_URL")
    }

    fun isDebugEnable(): Boolean {
        val debug = System.getenv("DEBUG") ?: return false
        return when (debug.toLowerCase()) {
            "1", "true", "on" -> true
            else -> false
        }
    }

    private fun getEnv(envKey: String): String {
        return System.getenv(envKey) ?: throw IllegalStateException("$envKey is not defined.")
    }
}
