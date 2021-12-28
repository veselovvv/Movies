package com.veselovvv.movies.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.veselovvv.movies.data.Movie
import com.veselovvv.movies.data.repositories.NetworkState
import com.veselovvv.movies.ui.repositories.MoviePagedListRepository
import io.reactivex.disposables.CompositeDisposable

class MainActivityViewModel(private val movieRepository: MoviePagedListRepository) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val moviePagedList: LiveData<PagedList<Movie>> by lazy {
        movieRepository.fetchMoviePagedList(compositeDisposable)
    }

    val networkState: LiveData<NetworkState> by lazy {
        movieRepository.getNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return moviePagedList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.dispose()
    }
}