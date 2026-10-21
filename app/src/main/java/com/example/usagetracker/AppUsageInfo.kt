package com.example.usagetracker

import android.graphics.drawable.Drawable

data class AppUsageInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable?,
    val totalTimeMs: Long
)
