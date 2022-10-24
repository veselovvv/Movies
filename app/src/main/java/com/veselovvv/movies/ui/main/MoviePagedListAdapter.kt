package com.veselovvv.movies.ui.main

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
import com.veselovvv.movies.data.api.MovieDBClient
import com.veselovvv.movies.data.models.Movie
import com.veselovvv.movies.ui.core.UI

class MoviePagedListAdapter(private val startActivityWithIdParameter: (id: Int) -> Unit)
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
            (holder as MovieItemViewHolder).bind(getItem(position), startActivityWithIdParameter)
        else
            (holder as NetworkStateItemViewHolder).bind(networkState)

    override fun getItemCount() = super.getItemCount() + if (hasExtraRow()) 1 else 0

    override fun getItemViewType(position: Int) =
        if (hasExtraRow() && position == itemCount - 1) NETWORK_VIEW_TYPE else MOVIE_VIEW_TYPE

    fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    fun getSpanSize(position: Int) =
        if (getItemViewType(position) == MOVIE_VIEW_TYPE) MOVIE_VIEW_TYPE_SPAN else NETWORK_VIEW_TYPE_SPAN

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()

        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            val itemCount = super.getItemCount()
            if (hadExtraRow) notifyItemRemoved(itemCount) else notifyItemInserted(itemCount)
        } else
            if (hasExtraRow && previousState != networkState) notifyItemChanged(itemCount - 1)
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.getId() == newItem.getId()
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }

    class MovieItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(movie: Movie?, startActivityWithIdParameter: (id: Int) -> Unit) {
            itemView.findViewById<MaterialTextView>(R.id.cv_title).text = movie?.getTitle()
            itemView.findViewById<MaterialTextView>(R.id.cv_release_date).text = movie?.getReleaseDate()

            val moviePosterURL = MovieDBClient.getPosterBaseUrl() + movie?.getPosterPath()

            Glide.with(itemView.context)
                .load(moviePosterURL)
                .into(itemView.findViewById(R.id.cv_movie_poster))

            itemView.setOnClickListener {
                startActivityWithIdParameter.invoke(movie?.getId() ?: 0)
            }
        }
    }

    class NetworkStateItemViewHolder(view: View) : RecyclerView.ViewHolder(view), UI {
        fun bind(networkState: NetworkState?) {
            val makeProgressIndicatorVisible = networkState.isTypeOf(NetworkState.LOADING)
            itemView.findViewById<CircularProgressIndicator>(R.id.progress_indicator_item)
                .makeVisible(makeProgressIndicatorVisible)

            itemView.findViewById<MaterialTextView>(R.id.error_item).apply {
                if (networkState.isTypeOf(NetworkState.ERROR) || networkState.isTypeOf(NetworkState.END_OF_LIST)) {
                    makeVisible()
                    text = networkState?.getMessage()
                } else
                    makeVisible(false)
            }
        }

        fun NetworkState?.isTypeOf(networkState: NetworkState) = this != null && this == networkState
    }

    companion object {
        private const val MOVIE_VIEW_TYPE = 1
        private const val NETWORK_VIEW_TYPE = 2
        private const val MOVIE_VIEW_TYPE_SPAN = 1 // MOVIE_VIEW_TYPE needs 1 out of 2 span
        private const val NETWORK_VIEW_TYPE_SPAN = 2 // NETWORK_VIEW_TYPE needs all 2 span
    }
}