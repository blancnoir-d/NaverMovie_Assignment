package me.saeha.android.navermovie_project.model

class MoviesData(
    val lastBuildDate: String,
    val total: String,
    start: String,
    display: String,
    items: Array<Movie>
) {
    data class Movie(
        val title: String,
        val link: String,
        val image: String,
        val subtitle: String,
        val pubDate: String,
        val director: String,
        val actor: String,
        val userRating: String
    )
}
