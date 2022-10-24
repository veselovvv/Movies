package com.veselovvv.movies.ui.movie

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.veselovvv.movies.R
import com.veselovvv.movies.data.NetworkState
import com.veselovvv.movies.data.api.MovieDBClient
import com.veselovvv.movies.data.models.MovieDetails
import com.veselovvv.movies.data.repositories.MovieDetailsRepository
import com.veselovvv.movies.makeVisible
import com.veselovvv.movies.ui.core.BaseActivity

class MovieActivity : BaseActivity() {
    private lateinit var movieDetailsRepository: MovieDetailsRepository
    private lateinit var viewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        movieDetailsRepository = MovieDetailsRepository(MovieDBClient.getClient())

        val movieId = intent.getIntExtra(getIdParamKey(), 1)
        // TODO DRY
        viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>) =
                MovieViewModel(movieDetailsRepository, movieId) as T
        })[MovieViewModel::class.java]

        viewModel.movieDetails.observe(this) { movieDetails ->
            setTextForTextViews(movieDetails)

            val posterURL = MovieDBClient.getPosterBaseUrl() + movieDetails.getPosterPath()
            Glide.with(this).load(posterURL).into(findViewById(R.id.movie_poster))
        }

        viewModel.networkState.observe(this) { networkState ->
            findViewById<LinearLayout>(R.id.data_layout)
                .makeVisible(networkState != NetworkState.ERROR)

            findViewById<CircularProgressIndicator>(R.id.progress_indicator)
                .makeVisible(networkState == NetworkState.LOADING)

            findViewById<MaterialTextView>(R.id.error)
                .makeVisible(networkState == NetworkState.ERROR)
        }
    }

    fun setTextForTextViews(movieDetails: MovieDetails) {
        mapOf(
            Pair(R.id.title, movieDetails.getTitle()),
            Pair(R.id.subtitle, movieDetails.getTagline()),
            Pair(R.id.release_date, movieDetails.getReleaseDate()),
            Pair(R.id.rating, movieDetails.getRating()),
            Pair(R.id.runtime, movieDetails.getRuntime()),
            Pair(R.id.overview, movieDetails.getOverview()),
            Pair(R.id.budget, movieDetails.getBudget()),
            Pair(R.id.revenue, movieDetails.getRevenue())
        ).forEach { idWithText ->
            findViewById<MaterialTextView>(idWithText.key).text = idWithText.value
        }
    }
}