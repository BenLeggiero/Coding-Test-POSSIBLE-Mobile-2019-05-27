package org.bh.codingtest.possiblemobile.v2019_05_27

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.JsonReader
import android.view.*
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*
import kotlinx.android.synthetic.main.media_row_view.view.*
import org.bh.codingtest.possiblemobile.v2019_05_27.mediaRow.MediaItemJson
import org.bh.codingtest.possiblemobile.v2019_05_27.utilities.loadAsImage
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import android.content.Intent
import android.net.Uri


class ScrollingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://BenLeggiero.me"))
            startActivity(browserIntent)
        }

        AsyncTask.execute {
            val items =
                parseItemsFromJsonOnThisThread(URL(getString(R.string.booksJsonUrl)).openStream())
            runOnUiThread {
                media_items_recycler_view.displayMedia(items)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun parseItemsFromJsonOnThisThread(jsonStream: InputStream): List<MediaItemJson> {
        val jsonReader = JsonReader(InputStreamReader(jsonStream))

        val items = mutableListOf<MediaItemJson>()

        jsonReader.beginArray()

        while (jsonReader.hasNext()) {
            MediaItemJson(jsonReader)?.let(items::add)
        }

        jsonReader.endArray()

        return items
    }


    private fun RecyclerView.displayMedia(items: List<MediaItemJson>) {

        class MediaLoadingAdapter(var allMediaItems: List<MediaItemJson>) :
            RecyclerView.Adapter<MediaLoadingAdapter.MediaViewHolder>() {

            inner class MediaViewHolder(val mediaRowView: LinearLayout) :
                RecyclerView.ViewHolder(mediaRowView)


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {

                val mediaRowView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.media_row_view, parent, false)
                        as LinearLayout

                val holder = MediaViewHolder(mediaRowView)
//                holder.setIsRecyclable(true)
                return holder
            }

            override fun getItemCount(): Int {
                return 200//allMediaItems.count()
            }

            override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
//                holder.setIsRecyclable(true)

                val currentItem = allMediaItems.getOrNull(position) ?: return

                holder.mediaRowView.titleTextView?.text = currentItem.title

                holder.mediaRowView.tidbitTextView?.text =
                    currentItem.author?.let { "Author: $it" }
                        ?: context.getString(R.string.defaultMediaTidbit)

                holder.mediaRowView.mediaThumbnailView?.let { imageView ->
                    AsyncTask.execute {
                        currentItem.imageUrl?.loadAsImage()?.let { imageDrawable ->
                            runOnUiThread {
                                imageView.setImageDrawable(imageDrawable)
                            }
                        }
                    }
                }
            }
        }

        this.setHasFixedSize(true)
        this.layoutManager = LinearLayoutManager(this@ScrollingActivity)
        this.adapter = MediaLoadingAdapter(items)
    }
}
