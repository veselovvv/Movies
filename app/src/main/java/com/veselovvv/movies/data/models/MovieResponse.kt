package com.veselovvv.movies.data.models

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("results")
    private val movieList: List<Movie>,
    @SerializedName("total_pages")
    private val totalPages: Int
) {
    fun getMovieList() = movieList
    fun getTotalPages() = totalPages
}