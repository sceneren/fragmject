package com.example.fragment.library.base.utils

import android.content.Context

object InjectUtils {

    fun Context.injectVConsoleJs(): String {
        return try {
            val vconsoleJs = resources.assets.open("js/vconsole.min.js").use {
                val buffer = ByteArray(it.available())
                it.read(buffer)
                String(buffer)
            }
            """ $vconsoleJs
                 var vConsole = new VConsole();
            """.trimIndent()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

}