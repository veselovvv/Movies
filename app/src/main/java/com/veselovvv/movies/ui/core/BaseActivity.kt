package com.veselovvv.movies.ui.core

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), UI {
    fun getIdParamKey() = ID_PARAM_KEY

    companion object {
        private const val ID_PARAM_KEY = "id"
    }
}