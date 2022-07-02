package com.veselovvv.movies.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.veselovvv.movies.R
import com.veselovvv.movies.data.api.MovieDBClient
import com.veselovvv.movies.data.api.MovieDBI
import com.veselovvv.movies.data.repositories.NetworkState
import com.veselovvv.movies.ui.adapters.MoviePagedListAdapter
import com.veselovvv.movies.ui.repositories.MoviePagedListRepository
import com.veselovvv.movies.ui.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    lateinit var movieRepository: MoviePagedListRepository
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiService: MovieDBI = MovieDBClient.getClient()
        movieRepository = MoviePagedListRepository(apiService)
        viewModel = getViewModel()

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
        viewModel.networkState.observe(this) {
            findViewById<ProgressBar>(R.id.progress_bar_popular).visibility =
                if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE

            findViewById<MaterialTextView>(R.id.error_popular).visibility =
                if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) movieAdapter.setNetworkState(it)
        }
    }

    private fun getViewModel(): MainActivityViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(movieRepository) as T
            }
        })[MainActivityViewModel::class.java]
    }
}