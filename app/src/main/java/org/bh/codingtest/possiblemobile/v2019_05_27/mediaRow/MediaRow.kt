package org.bh.codingtest.possiblemobile.v2019_05_27.mediaRow

import android.media.Image


data class MediaRow(
    val title: String,
    val author: String?,
    val image: Image?
) {
    companion object
}



data class MediaRowJson
