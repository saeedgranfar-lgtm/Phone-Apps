package com.positivenews.app.data

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Minimal RSS 2.0 reader. We only need the handful of fields the feed cards
 * render, so pulling in a full Rome-style library would be overkill.
 */
object RssParser {

    private val rfc822: ThreadLocal<SimpleDateFormat> = ThreadLocal.withInitial {
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }
    }

    private val imgRegex = Regex("""<img[^>]+src=["']([^"']+)["']""", RegexOption.IGNORE_CASE)

    fun parse(stream: InputStream, source: NewsSource): List<Article> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(stream, null)

        val out = mutableListOf<Article>()
        var inItem = false
        var title = ""
        var link = ""
        var description = ""
        var category: String? = null
        var pubDate = ""
        var imageUrl: String? = null

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    val name = parser.name
                    if (name.equals("item", true)) {
                        inItem = true
                        title = ""; link = ""; description = ""
                        category = null; pubDate = ""; imageUrl = null
                    } else if (inItem) {
                        when {
                            name.equals("title", true) -> title = readText(parser)
                            name.equals("link", true) -> link = readText(parser)
                            name.equals("description", true) -> description = readText(parser)
                            name.equals("category", true) -> category = readText(parser)
                            name.equals("pubDate", true) -> pubDate = readText(parser)
                            name.equals("media:content", true) ||
                                name.equals("media:thumbnail", true) -> {
                                val url = parser.getAttributeValue(null, "url")
                                if (!url.isNullOrBlank() && imageUrl == null) imageUrl = url
                            }
                            name.equals("enclosure", true) -> {
                                val type = parser.getAttributeValue(null, "type") ?: ""
                                if (type.startsWith("image", true)) {
                                    val url = parser.getAttributeValue(null, "url")
                                    if (!url.isNullOrBlank() && imageUrl == null) imageUrl = url
                                }
                            }
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name.equals("item", true) && inItem) {
                        inItem = false
                        if (imageUrl == null) {
                            imageUrl = imgRegex.find(description)?.groupValues?.getOrNull(1)
                        }
                        val cleanDesc = description.replace(Regex("<[^>]+>"), "").trim()
                        out += Article(
                            title = title.trim(),
                            link = link.trim(),
                            description = cleanDesc,
                            source = source,
                            category = category?.trim(),
                            imageUrl = imageUrl,
                            publishedAtMillis = parsePubDate(pubDate),
                            positivityScore = PositivityFilter.score(title, cleanDesc),
                        )
                    }
                }
            }
            event = parser.next()
        }
        return out
    }

    private fun readText(parser: XmlPullParser): String {
        var text = ""
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.text
            parser.nextTag()
        }
        return text
    }

    private fun parsePubDate(value: String): Long {
        if (value.isBlank()) return System.currentTimeMillis()
        return runCatching { rfc822.get()!!.parse(value)?.time }
            .getOrNull() ?: System.currentTimeMillis()
    }
}
