package com.veselovvv.movies.ui.adapters

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
import com.veselovvv.movies.data.Movie
import com.veselovvv.movies.data.api.POSTER_BASE_URL
import com.veselovvv.movies.data.repositories.NetworkState
import com.veselovvv.movies.ui.activities.MovieActivity

class MoviePagedListAdapter(public val context: Context)
    : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    private var networkState: NetworkState? = null

    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val layoutInflater = LayoutInflater.from(parent.context)

        if (viewType == MOVIE_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.list_item, parent, false)
            return MovieItemViewHolder(view)
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MOVIE_VIEW_TYPE) {
            (holder as MovieItemViewHolder).bind(getItem(position), context)
        } else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) NETWORK_VIEW_TYPE else MOVIE_VIEW_TYPE
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()

        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) notifyItemRemoved(super.getItemCount())
            else notifyItemInserted(super.getItemCount())
        } else if (hasExtraRow && previousState != networkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {

        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
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
            if (networkState != null && networkState == NetworkState.LOADING) {
                itemView.findViewById<CircularProgressIndicator>(R.id.progress_indicator_item)
                    .visibility = View.VISIBLE
            } else {
                itemView.findViewById<CircularProgressIndicator>(R.id.progress_indicator_item)
                    .visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                itemView.findViewById<MaterialTextView>(R.id.error_item).apply {
                    visibility = View.VISIBLE
                    text = networkState.message
                }
            } else if (networkState != null && networkState == NetworkState.ENDOFLIST) {
                itemView.findViewById<MaterialTextView>(R.id.error_item).apply {
                    visibility = View.VISIBLE
                    text = networkState.message
                }
            } else {
                itemView.findViewById<MaterialTextView>(R.id.error_item).visibility = View.GONE
            }
        }
    }
}