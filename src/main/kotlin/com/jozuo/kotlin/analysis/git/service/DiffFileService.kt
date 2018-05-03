package com.jozuo.kotlin.analysis.git.service

import com.jozuo.kotlin.analysis.git.model.ChangeFileListBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
class DiffFileService {

    @Autowired
    private lateinit var builder: ChangeFileListBuilder

    fun execute(destPath: String) {
        val changeFileList = builder.build()
        File(destPath, "diff-file-revision.txt").printWriter().use {
            changeFileList.changeFiles.forEach { changeFile ->
                it.println("${changeFile.name},${changeFile.commitHash}")
            }
        }
    }
}