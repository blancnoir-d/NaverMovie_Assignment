package me.saeha.android.navermovie_project.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class Movie() : RealmObject(),Serializable {
    @PrimaryKey
    var id: Int? = 0
    var title: String? = null
    var link: String? = null
    var image: String? = null
    var subtitle: String? = null
    var pubDate: String? = null
    var director: String? = null
    var actor: String? = null
    var userRating: String? = null
    var favorite: Boolean = false
    var code: Int = 0
}