package com.veselovvv.movies

import android.view.View

fun View.makeVisible(visible: Boolean = true) {
    visibility = if (visible) View.VISIBLE else View.GONE
}