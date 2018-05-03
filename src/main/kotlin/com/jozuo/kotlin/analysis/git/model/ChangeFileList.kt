package com.jozuo.kotlin.analysis.git.model

class ChangeFileList(builder: ChangeFileListBuilder) {
    var changeFiles: List<ChangeFile> = builder.changeFiles
        private set
}