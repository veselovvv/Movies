package com.veselovvv.movies.data.models

import com.google.gson.annotations.SerializedName
import java.text.NumberFormat
import java.util.*

data class MovieDetails(
    private val budget: Int,
    private val overview: String,
    @SerializedName("poster_path")
    private val posterPath: String,
    @SerializedName("release_date")
    private val releaseDate: String,
    private val revenue: Long,
    private val runtime: Int,
    private val tagline: String,
    private val title: String,
    @SerializedName("vote_average")
    private val rating: Double
) {
    fun getBudget() = formatNumberAsString(budget)
    fun getOverview() = overview
    fun getPosterPath() = posterPath
    fun getReleaseDate() = releaseDate
    fun getRevenue() = formatNumberAsString(revenue)
    fun getRuntime() = runtime.toString()
    fun getTagline() = tagline
    fun getTitle() = title
    fun getRating() = rating.toString()

    fun formatNumberAsString(number: Number) =
        NumberFormat.getCurrencyInstance(Locale.US).format(number).toString()
}