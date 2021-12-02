package com.binzee.foxlib.lib_kotlin.utils.file

import java.io.File

/**
 * 文件工具
 *
 * @author tong.xw
 * 2021/12/01 10:48
 */
object FileUtils {

    /**
     * 获取或创建子文件夹
     */
    fun getOrMakeChildDir(parent: File, subDirName: String): File {
        return File(parent, subDirName).also {
            if (!it.exists()) it.mkdir()
        }
    }

    /**
     * 获取或创建子文件
     */
    fun getOrMakeChildFile(parent: File, fileName: String): File {
        return File(parent, fileName).also {
            if (!it.exists()) it.createNewFile()
        }
    }
}