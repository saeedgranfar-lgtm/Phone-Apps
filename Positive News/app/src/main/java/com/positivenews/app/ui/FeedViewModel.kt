package com.positivenews.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.positivenews.app.data.Article
import com.positivenews.app.data.NewsRepository
import com.positivenews.app.data.NewsSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface FeedState {
    data object Loading : FeedState
    data class Ready(val articles: List<Article>) : FeedState
    data class Error(val message: String) : FeedState
}

class FeedViewModel(
    private val repo: NewsRepository = NewsRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow<FeedState>(FeedState.Loading)
    val state: StateFlow<FeedState> = _state.asStateFlow()

    private val _sourceFilter = MutableStateFlow<NewsSource?>(null)
    val sourceFilter: StateFlow<NewsSource?> = _sourceFilter.asStateFlow()

    init { refresh() }

    fun refresh() {
        _state.value = FeedState.Loading
        viewModelScope.launch {
            _state.value = try {
                val items = repo.loadPositiveFeed()
                if (items.isEmpty()) FeedState.Error("No uplifting headlines right now. Try again soon.")
                else FeedState.Ready(items)
            } catch (t: Throwable) {
                FeedState.Error(t.message ?: "Couldn't reach the feeds.")
            }
        }
    }

    fun setSource(filter: NewsSource?) { _sourceFilter.update { filter } }
}
