package com.veselovvv.movies

import android.view.View

// TODO move to UI interface with default implementation or abstract class?
fun View.makeVisible(visible: Boolean = true) {
    visibility = if (visible) View.VISIBLE else View.GONE
}