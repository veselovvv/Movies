package com.veselovvv.movies.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.veselovvv.movies.data.NetworkState
import com.veselovvv.movies.data.models.Movie
import com.veselovvv.movies.data.repositories.MoviePagedListRepository
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(private val movieRepository: MoviePagedListRepository) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val moviePagedList: LiveData<PagedList<Movie>> by lazy {
        movieRepository.fetchMoviePagedList(compositeDisposable)
    }

    val networkState: LiveData<NetworkState> by lazy {
        movieRepository.getNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun listIsEmpty() = moviePagedList.value?.isEmpty() ?: true
}