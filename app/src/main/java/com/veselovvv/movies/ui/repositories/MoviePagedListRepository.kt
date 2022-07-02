package com.veselovvv.movies.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.veselovvv.movies.data.Movie
import com.veselovvv.movies.data.api.MovieDBI
import com.veselovvv.movies.data.repositories.MovieDataSource
import com.veselovvv.movies.data.repositories.MovieDataSourceFactory
import com.veselovvv.movies.data.repositories.NetworkState
import io.reactivex.disposables.CompositeDisposable

class MoviePagedListRepository(private val apiService: MovieDBI) {
    lateinit var movieDataSourceFactory: MovieDataSourceFactory

    fun fetchMoviePagedList(compositeDisposable: CompositeDisposable): LiveData<PagedList<Movie>> {
        movieDataSourceFactory = MovieDataSourceFactory(apiService, compositeDisposable)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        return LivePagedListBuilder(movieDataSourceFactory, config).build()
    }

    fun getNetworkState(): LiveData<NetworkState> = Transformations.switchMap(
        movieDataSourceFactory.moviesLiveDataSource, MovieDataSource::networkState
    )

    companion object {
        private const val POST_PER_PAGE = 20
    }
}