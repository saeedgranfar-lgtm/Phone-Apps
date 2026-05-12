package com.positivenews.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.positivenews.app.data.Article
import java.text.DateFormat
import java.util.Date

@Composable
fun ArticleCard(article: Article) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                runCatching {
                    CustomTabsIntent.Builder().setShowTitle(true).build()
                        .launchUrl(context, Uri.parse(article.link))
                }.onFailure {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(article.link)))
                }
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    ) {
        Column {
            if (!article.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(article.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SourceBadge(article.source.displayName)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = formatTime(article.publishedAtMillis),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(8.dp))
                    PositivityPill(article.positivityScore)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                if (article.description.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = article.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceBadge(name: String) {
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(name, style = MaterialTheme.typography.labelMedium) },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@Composable
private fun PositivityPill(score: Int) {
    val bg: Color
    val fg: Color
    val label: String
    when {
        score >= 4 -> {
            label = "Uplifting"
            bg = MaterialTheme.colorScheme.tertiaryContainer
            fg = MaterialTheme.colorScheme.onTertiaryContainer
        }
        score >= 2 -> {
            label = "Positive"
            bg = MaterialTheme.colorScheme.secondaryContainer
            fg = MaterialTheme.colorScheme.onSecondaryContainer
        }
        else -> {
            label = "Hopeful"
            bg = MaterialTheme.colorScheme.surfaceContainerHigh
            fg = MaterialTheme.colorScheme.onSurface
        }
    }
    Surface(
        color = bg,
        contentColor = fg,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

private fun formatTime(millis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - millis
    val mins = diff / 60_000
    return when {
        mins < 1 -> "just now"
        mins < 60 -> "${mins}m ago"
        mins < 60 * 24 -> "${mins / 60}h ago"
        else -> DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(millis))
    }
}
