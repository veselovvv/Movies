package com.veselovvv.movies.data.repositories

import androidx.lifecycle.LiveData
import com.veselovvv.movies.data.api.MovieDBI
import com.veselovvv.movies.data.datasources.MovieDetailsNetworkDataSource
import com.veselovvv.movies.data.models.MovieDetails
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsRepository(private val apiService: MovieDBI) {
    private lateinit var movieDetailsNetworkDataSource: MovieDetailsNetworkDataSource

    fun getMovieDetailsNetworkState() = movieDetailsNetworkDataSource.networkState

    fun fetchMovieDetails(
        compositeDisposable: CompositeDisposable,
        movieId: Int
    ): LiveData<MovieDetails> {
        movieDetailsNetworkDataSource = MovieDetailsNetworkDataSource(apiService, compositeDisposable)
        movieDetailsNetworkDataSource.fetchMovieDetails(movieId)
        return movieDetailsNetworkDataSource.downloadedMovieDetailsResponse
    }
}