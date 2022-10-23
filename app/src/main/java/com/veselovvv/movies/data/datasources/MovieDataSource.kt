package com.veselovvv.movies.data.datasources

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.veselovvv.movies.data.NetworkState
import com.veselovvv.movies.data.api.MovieDBI
import com.veselovvv.movies.data.models.Movie
import com.veselovvv.movies.data.models.MovieResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MovieDataSource(
    private val apiService: MovieDBI,
    private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, Movie>() {
    val networkState: MutableLiveData<NetworkState> = MutableLiveData()
    private var page = FIRST_PAGE

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) = load(page) {
        callback.onResult(it.getMovieList(), null, page + 1)
        networkState.postValue(NetworkState.LOADED)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) =
        load(params.key) {
            // If there is content to load, load it, else - the end of the list:
            if (it.getTotalPages() >= params.key) {
                callback.onResult(it.getMovieList(), params.key + 1)
                networkState.postValue(NetworkState.LOADED)
            } else
                networkState.postValue(NetworkState.END_OF_LIST)
        }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) = Unit

    fun load(pageNumber: Int, movieResponse: (MovieResponse) -> Unit) {
        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.getPopularMovie(pageNumber)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    movieResponse.invoke(it)
                }, {
                    networkState.postValue(NetworkState.ERROR)
                })
        )
    }

    companion object {
        private const val FIRST_PAGE = 1
    }
}