package me.saeha.android.navermovie_project.model

class MoviesData(
    var lastBuildDate: String,
    var total: String,
    var start: String,
    var display: String,
    var items: Array<Movie>
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
