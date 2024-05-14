package com.msa.eshop.ui.navigation.bottomNav.util

fun lerp(start: Float, stop: Float, fraction: Float) =
    (start * (1 - fraction) + stop * fraction)