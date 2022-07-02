package com.veselovvv.movies.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.veselovvv.movies.R
import com.veselovvv.movies.data.MovieDetails
import com.veselovvv.movies.data.api.MovieDBClient
import com.veselovvv.movies.data.api.MovieDBI
import com.veselovvv.movies.data.api.POSTER_BASE_URL
import com.veselovvv.movies.data.repositories.NetworkState
import com.veselovvv.movies.ui.repositories.MovieDetailsRepository
import com.veselovvv.movies.ui.viewmodels.MovieViewModel
import java.text.NumberFormat
import java.util.*

class MovieActivity : AppCompatActivity() {
    private lateinit var movieRepository: MovieDetailsRepository
    private lateinit var viewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val movieId = intent.getIntExtra("id", 1)
        val apiService: MovieDBI = MovieDBClient.getClient()

        movieRepository = MovieDetailsRepository(apiService)
        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this) { bindUI(it) }
        viewModel.networkState.observe(this) {
            findViewById<CircularProgressIndicator>(R.id.progress_indicator).visibility =
                if (it == NetworkState.LOADING) View.VISIBLE else View.GONE

            findViewById<MaterialTextView>(R.id.error).visibility =
                if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        }
    }

    private fun bindUI(it: MovieDetails) {
        findViewById<MaterialTextView>(R.id.title).text = it.title
        findViewById<MaterialTextView>(R.id.subtitle).text = it.tagline
        findViewById<MaterialTextView>(R.id.release_date).text = it.releaseDate
        findViewById<MaterialTextView>(R.id.rating).text = it.rating.toString()
        findViewById<MaterialTextView>(R.id.runtime).text = it.runtime.toString()
        findViewById<MaterialTextView>(R.id.overview).text = it.overview

        val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)
        val posterURL = POSTER_BASE_URL + it.posterPath

        findViewById<MaterialTextView>(R.id.budget).text = formatCurrency.format(it.budget)
        findViewById<MaterialTextView>(R.id.revenue).text = formatCurrency.format(it.revenue)
        Glide.with(this).load(posterURL).into(findViewById(R.id.movie_poster))
    }

    private fun getViewModel(movieId: Int) =
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MovieViewModel(movieRepository, movieId) as T
            }
        })[MovieViewModel::class.java]
}