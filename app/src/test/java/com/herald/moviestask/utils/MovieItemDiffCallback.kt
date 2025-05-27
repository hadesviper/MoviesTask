package com.herald.moviestask.utils

import androidx.recyclerview.widget.DiffUtil
import com.herald.moviestask.domain.models.MoviesModel

class MovieItemDiffCallback : DiffUtil.ItemCallback<MoviesModel.MovieItem>() {
    override fun areItemsTheSame(oldItem: MoviesModel.MovieItem, newItem: MoviesModel.MovieItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MoviesModel.MovieItem, newItem: MoviesModel.MovieItem): Boolean {
        return oldItem == newItem
    }
}