package com.veselovvv.movies.data

class NetworkState(private val message: String) {
    fun getMessage() = message

    companion object {
        val LOADED = NetworkState("Success")
        val LOADING = NetworkState("Running")
        val ERROR = NetworkState("Error")
        val END_OF_LIST = NetworkState("End of list")
    }
}