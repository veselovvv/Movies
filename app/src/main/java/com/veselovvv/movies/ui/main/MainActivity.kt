package com.veselovvv.movies.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.veselovvv.movies.R
import com.veselovvv.movies.data.NetworkState
import com.veselovvv.movies.data.api.MovieDBClient
import com.veselovvv.movies.data.repositories.MoviePagedListRepository
import com.veselovvv.movies.ui.core.BaseActivity
import com.veselovvv.movies.ui.movie.MovieActivity

class MainActivity : BaseActivity() {
    private lateinit var movieRepository: MoviePagedListRepository
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieRepository = MoviePagedListRepository(MovieDBClient.getClient())
        viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>) =
                MainViewModel(movieRepository) as T
        })[MainViewModel::class.java]

        val movieAdapter = MoviePagedListAdapter { id ->
            val intent = Intent(this, MovieActivity::class.java)
            intent.putExtra(getIdParamKey(), id)
            startActivity(intent)
        }

        val gridLayoutManager = GridLayoutManager(this, AMOUNT_OF_ITEMS_IN_ROW)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = movieAdapter.getSpanSize(position)
        }

        findViewById<RecyclerView>(R.id.movie_list).apply {
            layoutManager = gridLayoutManager
            setHasFixedSize(true)
            adapter = movieAdapter
        }

        viewModel.moviePagedList.observe(this) { movieAdapter.submitList(it) }
        viewModel.networkState.observe(this) { networkState ->
            val isListEmpty = viewModel.listIsEmpty()

            findViewById<ProgressBar>(R.id.progress_bar_popular)
                .makeVisible(isListEmpty && networkState == NetworkState.LOADING)

            findViewById<MaterialTextView>(R.id.error_popular)
                .makeVisible(isListEmpty && networkState == NetworkState.ERROR)

            if (!isListEmpty) movieAdapter.setNetworkState(networkState)
        }
    }

    companion object {
        private const val AMOUNT_OF_ITEMS_IN_ROW = 2
    }
}