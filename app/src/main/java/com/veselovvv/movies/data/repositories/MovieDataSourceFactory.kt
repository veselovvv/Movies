package com.veselovvv.movies.data.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.veselovvv.movies.data.Movie
import com.veselovvv.movies.data.api.MovieDBI
import io.reactivex.disposables.CompositeDisposable

class MovieDataSourceFactory(
    private val apiService: MovieDBI,
    private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, Movie>() {
    val moviesLiveDataSource = MutableLiveData<MovieDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val movieDataSource = MovieDataSource(apiService, compositeDisposable)
        moviesLiveDataSource.postValue(movieDataSource)
        return movieDataSource
    }
}