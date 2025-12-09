package org.whynot.kipku.data

import androidx.compose.ui.graphics.Color

data class FeatureItem(
    val title: String,
    val icon: Int,
    val color: Color,
    val destination:String = ""
)
