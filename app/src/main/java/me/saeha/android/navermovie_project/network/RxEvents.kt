package me.saeha.android.navermovie_project.network

import me.saeha.android.navermovie_project.model.Movie
import me.saeha.android.navermovie_project.model.MovieData

class RxEvents {
    //영화 검색 결과 List에서 별을 눌렀을 때
    class EventFavoriteOfMainList(val movie: MovieData)

    //즐겨찾기 List에서 별을 눌렀을 때
    class EventFavoriteOfFavoriteList(val movie: Movie)

    //영화 상세 화면이 종료 될 때
    class EventDetailIntoOfBack()
}
