package com.datangic.api.mock

import java.io.File

class MockAsset {
    private val BASE_PATH = "app/src/test/java/cn/com/xxx/xxx/base/mocks/data"

    //User API对应的模拟json数据的文件路径
    val USER_DATA = BASE_PATH + "/userJson_test"

    //通过文件路径，读取Json数据
    fun readFile(path: String): String {
        val content = file2String(File(path))
        return content
    }

    //kotlin丰富的I/O API,我们可以通过file.readText（charset）直接获取结果
    fun file2String(f: File, charset: String = "UTF-8"): String {
        return f.readText(Charsets.UTF_8)
    }
}