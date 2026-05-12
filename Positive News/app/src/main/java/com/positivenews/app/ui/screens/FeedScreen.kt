package com.positivenews.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.positivenews.app.R
import com.positivenews.app.data.NewsSource
import com.positivenews.app.ui.FeedState
import com.positivenews.app.ui.FeedViewModel
import com.positivenews.app.ui.components.ArticleCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(vm: FeedViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val activeFilter by vm.sourceFilter.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Top
        ),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = vm::refresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            SourceChips(active = activeFilter, onPick = vm::setSource)
            Box(Modifier.fillMaxSize()) {
                when (val s = state) {
                    FeedState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )

                    is FeedState.Error -> Text(
                        s.message,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    is FeedState.Ready -> {
                        val visible = remember(s.articles, activeFilter) {
                            activeFilter?.let { f -> s.articles.filter { it.source == f } }
                                ?: s.articles
                        }
                        AnimatedVisibility(visible.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(
                                    start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(
                                    items = visible,
                                    key = { it.link.ifBlank { it.title } },
                                ) { ArticleCard(it) }
                            }
                        }
                        if (visible.isEmpty()) {
                            Text(
                                "No uplifting stories from this source right now.",
                                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceChips(active: NewsSource?, onPick: (NewsSource?) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = active == null,
                onClick = { onPick(null) },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(),
            )
        }
        items(NewsSource.values()) { src ->
            FilterChip(
                selected = active == src,
                onClick = { onPick(if (active == src) null else src) },
                label = { Text(src.displayName) },
            )
        }
        item { Spacer(Modifier.width(8.dp)) }
    }
}
