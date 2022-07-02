package com.veselovvv.movies.ui.repositories

import androidx.lifecycle.LiveData
import com.veselovvv.movies.data.MovieDetails
import com.veselovvv.movies.data.api.MovieDBI
import com.veselovvv.movies.data.repositories.MovieDetailsNetworkDataSource
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsRepository(private val apiService: MovieDBI) {
    lateinit var movieDetailsNetworkDataSource: MovieDetailsNetworkDataSource

    fun fetchMovieDetails(
        compositeDisposable: CompositeDisposable,
        movieId: Int
    ): LiveData<MovieDetails> {
        movieDetailsNetworkDataSource = MovieDetailsNetworkDataSource(apiService, compositeDisposable)
        movieDetailsNetworkDataSource.fetchMovieDetails(movieId)
        return movieDetailsNetworkDataSource.downloadedMovieDetailsResponse
    }

    fun getMovieDetailsNetworkState() = movieDetailsNetworkDataSource.networkState
}