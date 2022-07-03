package com.veselovvv.movies.ui.movie

import android.os.Bundle
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
import java.text.NumberFormat
import java.util.*

class MovieActivity : AppCompatActivity() {
    private lateinit var movieRepository: MovieDetailsRepository
    private lateinit var viewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val movieId = intent.getIntExtra("id", 1)
        val apiService = MovieDBClient.getClient()

        movieRepository = MovieDetailsRepository(apiService)
        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this) { bindUI(it) }
        viewModel.networkState.observe(this) {
            findViewById<CircularProgressIndicator>(R.id.progress_indicator)
                .makeVisible(it == NetworkState.LOADING)

            findViewById<MaterialTextView>(R.id.error)
                .makeVisible(it == NetworkState.ERROR)
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
            override fun <T : ViewModel?> create(modelClass: Class<T>) =
                MovieViewModel(movieRepository, movieId) as T
        })[MovieViewModel::class.java]
}