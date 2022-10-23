package com.veselovvv.movies.ui.movie

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.veselovvv.movies.R
import com.veselovvv.movies.data.NetworkState
import com.veselovvv.movies.data.api.MovieDBClient
import com.veselovvv.movies.data.api.POSTER_BASE_URL
import com.veselovvv.movies.data.models.MovieDetails
import com.veselovvv.movies.data.repositories.MovieDetailsRepository
import com.veselovvv.movies.makeVisible
import com.veselovvv.movies.ui.main.MoviePagedListAdapter

class MovieActivity : AppCompatActivity() {
    private lateinit var movieDetailsRepository: MovieDetailsRepository
    private lateinit var viewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        movieDetailsRepository = MovieDetailsRepository(MovieDBClient.getClient())

        val movieId = intent.getIntExtra(MoviePagedListAdapter.MovieItemViewHolder.ID_PARAM_KEY, 1)
        // TODO DRY
        viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>) =
                MovieViewModel(movieDetailsRepository, movieId) as T
        })[MovieViewModel::class.java]

        viewModel.movieDetails.observe(this) { bindUI(it) }
        viewModel.networkState.observe(this) {
            findViewById<CircularProgressIndicator>(R.id.progress_indicator)
                .makeVisible(it == NetworkState.LOADING)

            findViewById<MaterialTextView>(R.id.error)
                .makeVisible(it == NetworkState.ERROR)
        }
    }

    fun bindUI(movieDetails: MovieDetails) {
        setTextForTextView(R.id.title, movieDetails.getTitle())
        setTextForTextView(R.id.subtitle, movieDetails.getTagline())
        setTextForTextView(R.id.release_date, movieDetails.getReleaseDate())
        setTextForTextView(R.id.rating, movieDetails.getRating())
        setTextForTextView(R.id.runtime, movieDetails.getRuntime())
        setTextForTextView(R.id.overview, movieDetails.getOverview())
        setTextForTextView(R.id.budget, movieDetails.getBudget())
        setTextForTextView(R.id.revenue, movieDetails.getRevenue())

        val posterURL = POSTER_BASE_URL + movieDetails.getPosterPath()
        Glide.with(this).load(posterURL).into(findViewById(R.id.movie_poster))
    }

    fun setTextForTextView(@IdRes textViewId: Int, text: String) {
        findViewById<MaterialTextView>(textViewId).text = text
    }
}