package com.veselovvv.movies.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.veselovvv.movies.data.Movie
import com.veselovvv.movies.data.api.MovieDBI
import com.veselovvv.movies.data.api.POST_PER_PAGE
import com.veselovvv.movies.data.repositories.MovieDataSource
import com.veselovvv.movies.data.repositories.MovieDataSourceFactory
import com.veselovvv.movies.data.repositories.NetworkState
import io.reactivex.disposables.CompositeDisposable

class MoviePagedListRepository(private val apiService: MovieDBI) {

    lateinit var movieDataSourceFactory: MovieDataSourceFactory
    lateinit var moviePagedList: LiveData<PagedList<Movie>>

    fun fetchMoviePagedList(compositeDisposable: CompositeDisposable): LiveData<PagedList<Movie>> {
        movieDataSourceFactory = MovieDataSourceFactory(apiService, compositeDisposable)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        moviePagedList = LivePagedListBuilder(movieDataSourceFactory, config).build()
        return moviePagedList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<MovieDataSource, NetworkState>(
            movieDataSourceFactory.moviesLiveDataSource, MovieDataSource::networkState
        )
    }
}