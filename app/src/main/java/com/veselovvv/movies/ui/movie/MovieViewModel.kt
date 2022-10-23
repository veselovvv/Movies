package com.veselovvv.movies.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.veselovvv.movies.data.NetworkState
import com.veselovvv.movies.data.models.MovieDetails
import com.veselovvv.movies.data.repositories.MovieDetailsRepository
import io.reactivex.disposables.CompositeDisposable

class MovieViewModel(
    private val movieRepository: MovieDetailsRepository,
    private val movieId: Int
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val movieDetails: LiveData<MovieDetails> by lazy {
        movieRepository.fetchMovieDetails(compositeDisposable, movieId)
    }

    val networkState: LiveData<NetworkState> by lazy {
        movieRepository.getMovieDetailsNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}