package com.veselovvv.movies.data.models

import com.google.gson.annotations.SerializedName

data class Movie(
    private val id: Int,
    @SerializedName("poster_path")
    private val posterPath: String,
    @SerializedName("release_date")
    private val releaseDate: String,
    private val title: String
) {
    fun getId() = id
    fun getPosterPath() = posterPath
    fun getReleaseDate() = releaseDate
    fun getTitle() = title
}