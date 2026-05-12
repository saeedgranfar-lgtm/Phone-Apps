package com.positivenews.app.data

data class Article(
    val title: String,
    val link: String,
    val description: String,
    val source: NewsSource,
    val category: String?,
    val imageUrl: String?,
    val publishedAtMillis: Long,
    val positivityScore: Int,
)

enum class NewsSource(val displayName: String, val feedUrl: String) {
    BBC("BBC", "https://feeds.bbci.co.uk/news/rss.xml"),
    BBC_HEALTH("BBC Health", "https://feeds.bbci.co.uk/news/health/rss.xml"),
    BBC_SCIENCE("BBC Science", "https://feeds.bbci.co.uk/news/science_and_environment/rss.xml"),
    CNN("CNN", "http://rss.cnn.com/rss/edition.rss"),
    CNN_HEALTH("CNN Health", "http://rss.cnn.com/rss/edition_health.rss"),
    CNN_ENTERTAINMENT("CNN Entertainment", "http://rss.cnn.com/rss/edition_entertainment.rss"),
}
