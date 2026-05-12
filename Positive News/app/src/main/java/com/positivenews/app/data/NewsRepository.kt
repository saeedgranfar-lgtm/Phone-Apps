package com.positivenews.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class NewsRepository(
    private val client: OkHttpClient = defaultClient(),
) {

    suspend fun loadPositiveFeed(): List<Article> = withContext(Dispatchers.IO) {
        val sources = NewsSource.values().toList()
        val all = coroutineScope {
            sources.map { src -> async { fetchOne(src) } }.awaitAll()
        }.flatten()

        all.asSequence()
            .filter { it.positivityScore >= PositivityFilter.THRESHOLD }
            .distinctBy { it.link.ifBlank { it.title } }
            .sortedWith(
                compareByDescending<Article> { it.positivityScore }
                    .thenByDescending { it.publishedAtMillis }
            )
            .take(80)
            .toList()
    }

    private fun fetchOne(source: NewsSource): List<Article> = runCatching {
        val request = Request.Builder()
            .url(source.feedUrl)
            .header("User-Agent", "PositiveNews/1.0 (Android)")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@runCatching emptyList<Article>()
            response.body?.byteStream()?.use { RssParser.parse(it, source) } ?: emptyList()
        }
    }.getOrElse { emptyList() }

    companion object {
        private fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
}
