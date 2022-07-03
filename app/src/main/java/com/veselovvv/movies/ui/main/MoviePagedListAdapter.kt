package com.veselovvv.movies.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.veselovvv.movies.R
import com.veselovvv.movies.data.NetworkState
import com.veselovvv.movies.data.api.POSTER_BASE_URL
import com.veselovvv.movies.data.models.Movie
import com.veselovvv.movies.makeVisible
import com.veselovvv.movies.ui.movie.MovieActivity

class MoviePagedListAdapter(private val context: Context)
    : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {
    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == MOVIE_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.list_item, parent, false)
            MovieItemViewHolder(view)
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            NetworkStateItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        if (getItemViewType(position) == MOVIE_VIEW_TYPE)
            (holder as MovieItemViewHolder).bind(getItem(position), context)
        else
            (holder as NetworkStateItemViewHolder).bind(networkState)

    override fun getItemCount() = super.getItemCount() + if (hasExtraRow()) 1 else 0

    override fun getItemViewType(position: Int) =
        if (hasExtraRow() && position == itemCount - 1) NETWORK_VIEW_TYPE else MOVIE_VIEW_TYPE

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()

        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) notifyItemRemoved(super.getItemCount())
            else notifyItemInserted(super.getItemCount())
        } else if (hasExtraRow && previousState != networkState)
            notifyItemChanged(itemCount - 1)
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }

    class MovieItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(movie: Movie?, context: Context) {
            itemView.findViewById<MaterialTextView>(R.id.cv_title).text = movie?.title
            itemView.findViewById<MaterialTextView>(R.id.cv_release_date).text = movie?.releaseDate

            val moviePosterURL = POSTER_BASE_URL + movie?.posterPath

            Glide.with(itemView.context)
                .load(moviePosterURL)
                .into(itemView.findViewById(R.id.cv_movie_poster))

            itemView.setOnClickListener {
                val intent = Intent(context, MovieActivity::class.java)
                intent.putExtra("id", movie?.id)
                context.startActivity(intent)
            }
        }
    }

    class NetworkStateItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING)
                itemView.findViewById<CircularProgressIndicator>(R.id.progress_indicator_item)
                    .makeVisible()
            else itemView.findViewById<CircularProgressIndicator>(R.id.progress_indicator_item)
                    .makeVisible(false)

            if (networkState != null && networkState == NetworkState.ERROR)
                itemView.findViewById<MaterialTextView>(R.id.error_item).apply {
                    makeVisible()
                    text = networkState.message
            } else if (networkState != null && networkState == NetworkState.END_OF_LIST)
                itemView.findViewById<MaterialTextView>(R.id.error_item).apply {
                    makeVisible()
                    text = networkState.message
                }
            else itemView.findViewById<MaterialTextView>(R.id.error_item).makeVisible(false)
        }
    }

    companion object {
        private const val NETWORK_VIEW_TYPE = 2
        const val MOVIE_VIEW_TYPE = 1
    }
}