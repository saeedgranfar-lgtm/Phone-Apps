package com.positivenews.app

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.positivenews.app.ui.PositiveNewsRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // S25 Ultra runs a 1–120 Hz LTPO panel. Ask the compositor to give us
        // the high-rate path; the platform clamps if it can't oblige.
        window.attributes = window.attributes.apply { preferredRefreshRate = 120f }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            window.decorView.requestedFrameRate =
                View.REQUESTED_FRAME_RATE_CATEGORY_HIGH
        }

        setContent { PositiveNewsRoot() }
    }
}
