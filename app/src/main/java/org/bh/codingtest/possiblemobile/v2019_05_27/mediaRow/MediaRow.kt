package org.bh.codingtest.possiblemobile.v2019_05_27.mediaRow

import android.graphics.drawable.Drawable
import android.util.JsonReader
import org.bh.codingtest.possiblemobile.v2019_05_27.utilities.loadAsImage
import java.io.*
import java.net.URL


data class MediaRow(
    val title: String,
    val author: String?,
    val image: Drawable?
) {
    companion object {

        operator fun invoke(json: MediaItemJson): MediaRow {
            return MediaRow(
                title = json.title,
                author = json.author,
                image = json.imageUrl?.loadAsImage()
            )
        }
    }
}


data class MediaItemJson(
    var title: String,
    var author: String?,
    var imageUrl: URL?
) {

    companion object {

        const val titleSerialName = "title"
        const val authotSerialName = "author"
        const val imageUrlSerialName = "imageURL"


        operator fun invoke(jsonString: String) = MediaItemJson(JsonReader(StringReader(jsonString)))


        operator fun invoke(jsonStream: InputStream) = MediaItemJson(JsonReader(InputStreamReader(jsonStream)))


        operator fun invoke(jsonReader: JsonReader): MediaItemJson? {
            var title: String? = null
            var author: String? = null
            var imageUrl: String? = null

            jsonReader.beginObject()
            while (jsonReader.hasNext()) {

                val name = jsonReader.nextName()

                // We can assume that there is a next string, because our schema only has string values
                val nextString = jsonReader.nextString()

                when (name) {
                    titleSerialName -> title = nextString
                    authotSerialName -> author = nextString
                    imageUrlSerialName -> imageUrl = nextString.replace(Regex("^http\\b"), replacement = "https")
                }
            }
            jsonReader.endObject()

            if (null == title) {
                // Our schema requires a title
                return null
            }

            return MediaItemJson(
                title = title,
                author = author,
                imageUrl = imageUrl?.let { URL(it) }
            )
        }
    }
}
