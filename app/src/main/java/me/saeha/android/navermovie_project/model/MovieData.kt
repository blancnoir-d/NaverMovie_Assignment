package me.saeha.android.navermovie_project.model

data class MovieData(
    val title: String,
    val link: String,
    val image: String,
    val subtitle: String,
    val pubDate: String,
    val director: String,
    val actor: String,
    val userRating: String,
    val favorite: Boolean
)