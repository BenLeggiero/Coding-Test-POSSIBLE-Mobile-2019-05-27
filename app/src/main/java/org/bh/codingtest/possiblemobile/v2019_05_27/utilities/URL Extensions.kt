package org.bh.codingtest.possiblemobile.v2019_05_27.utilities

import android.graphics.drawable.Drawable
import android.util.Log
import org.bh.codingtest.possiblemobile.v2019_05_27.defaultLoggingTag
import java.io.InputStream
import java.net.URL

fun URL.loadAsImage(): Drawable? {
    try {
        val inputStr = content as? InputStream ?: return null
        return Drawable.createFromStream(inputStr, "thumbnail")
    }
    catch (error: Throwable) {
        Log.e(
            defaultLoggingTag,
            "Failed to load image from $this",
            error
        )
        return null
    }
}