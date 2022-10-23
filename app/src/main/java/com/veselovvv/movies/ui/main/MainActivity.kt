package com.veselovvv.movies.ui.main

import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
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
import com.veselovvv.movies.makeVisible

class MainActivity : AppCompatActivity() {
    private lateinit var movieRepository: MoviePagedListRepository
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieRepository = MoviePagedListRepository(MovieDBClient.getClient())
        // TODO DRY
        viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>) =
                MainViewModel(movieRepository) as T
        })[MainViewModel::class.java]

        val movieAdapter = MoviePagedListAdapter(this)
        val gridLayoutManager = GridLayoutManager(this, 2)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = movieAdapter.getItemViewType(position)
                // MOVIE_VIEW_TYPE needs 1 out of 2 span, NETWORK_VIEW_TYPE - all 2 span:
                return if (viewType == MoviePagedListAdapter.MOVIE_VIEW_TYPE) 1 else 2
            }
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
}