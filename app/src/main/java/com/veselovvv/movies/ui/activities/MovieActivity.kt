package com.veselovvv.movies.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
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

    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var error: MaterialTextView
    private lateinit var title: MaterialTextView
    private lateinit var subtitle: MaterialTextView
    private lateinit var releaseDate: MaterialTextView
    private lateinit var rating: MaterialTextView
    private lateinit var runtime: MaterialTextView
    private lateinit var budget: MaterialTextView
    private lateinit var revenue: MaterialTextView
    private lateinit var overview: MaterialTextView
    private lateinit var moviePoster: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        initComponents()

        val movieId = intent.getIntExtra("id", 1)
        val apiService: MovieDBI = MovieDBClient.getClient()

        movieRepository = MovieDetailsRepository(apiService)
        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this, Observer { bindUI(it) })
        viewModel.networkState.observe(this, Observer {
            progressIndicator.visibility =
                if (it == NetworkState.LOADING) View.VISIBLE else View.GONE

            error.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })
    }

    private fun initComponents() {
        progressIndicator = findViewById(R.id.progress_indicator)
        error = findViewById(R.id.error)
        title = findViewById(R.id.title)
        subtitle = findViewById(R.id.subtitle)
        releaseDate = findViewById(R.id.release_date)
        rating = findViewById(R.id.rating)
        runtime = findViewById(R.id.runtime)
        budget = findViewById(R.id.budget)
        revenue = findViewById(R.id.revenue)
        overview = findViewById(R.id.overview)
        moviePoster = findViewById(R.id.movie_poster)
    }

    fun bindUI(it: MovieDetails) {
        title.text = it.title
        subtitle.text = it.tagline
        releaseDate.text = it.releaseDate
        rating.text = it.rating.toString()
        runtime.text = it.runtime.toString()
        overview.text = it.overview

        val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)
        val posterURL = POSTER_BASE_URL + it.posterPath

        budget.text = formatCurrency.format(it.budget)
        revenue.text = formatCurrency.format(it.revenue)

        Glide.with(this).load(posterURL).into(moviePoster)
    }

    private fun getViewModel(movieId: Int): MovieViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MovieViewModel(movieRepository, movieId) as T
            }
        })[MovieViewModel::class.java]
    }
}