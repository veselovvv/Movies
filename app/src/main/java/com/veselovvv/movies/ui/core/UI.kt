package com.veselovvv.movies.ui.core

import android.view.View

interface UI {
    fun View.makeVisible(visible: Boolean = true) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }
}