package com.positivenews.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.positivenews.app.ui.screens.FeedScreen
import com.positivenews.app.ui.theme.PositiveNewsTheme

@Composable
fun PositiveNewsRoot() {
    PositiveNewsTheme {
        val vm: FeedViewModel = viewModel()
        FeedScreen(vm)
    }
}
