package me.saeha.android.navermovie_project.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import me.saeha.android.navermovie_project.model.Movie
import me.saeha.android.navermovie_project.model.MovieData
import me.saeha.android.navermovie_project.model.MoviesData
import me.saeha.android.navermovie_project.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {

    //영화 검색 결과
    private var searchResult = MutableLiveData<MutableList<MovieData>>()
    val liveSearchResult: LiveData<MutableList<MovieData>>
        get() = searchResult

    private var movieSearchResult: MutableList<MovieData> = arrayListOf()

    //즐겨찾기 목록
    private var favoriteList = MutableLiveData<MutableList<Movie>>()
    val liveFavoriteList: LiveData<MutableList<Movie>>
        get() = favoriteList

    private val CLIENT_ID = "6GXwvLWETQz9pjFapwto"
    private val CLIENT_SECRET = "YC92nIro4P"

    //Realm
    var realm: Realm

    init {
        searchResult.value = movieSearchResult
        realm = Realm.getDefaultInstance()
        favoriteList.value = getModelList()
    }

    //저장된 즐겨찾기 정보 가져오기
    private fun getModelList(): MutableList<Movie> {
        val list: MutableList<Movie> = ArrayList()
        try {
            realm = Realm.getDefaultInstance()
            val results: RealmResults<Movie> = realm
                .where(Movie::class.java)
                .findAll()
            list.addAll(realm.copyFromRealm(results))
        } finally {
            realm.close()
        }
        return list
    }

    //즐겨찾기 추가
    fun addFavorite(favoriteMovie: MovieData) {
        //체크 상태 변경
        movieSearchResult.forEach {
            if (it.code == favoriteMovie.code) {
                it.favorite = favoriteMovie.favorite
            }
        }
        searchResult.value = movieSearchResult
            realm.executeTransactionAsync {
                with(it.createObject(Movie::class.java, favoriteMovie.code)) {
                    this.title = favoriteMovie.title
                    this.link = favoriteMovie.link
                    this.image = favoriteMovie.image
                    this.subtitle = favoriteMovie.subtitle
                    this.pubDate = favoriteMovie.pubDate
                    this.director = favoriteMovie.director
                    this.actor = favoriteMovie.actor
                    this.userRating = favoriteMovie.userRating
                    this.favorite = favoriteMovie.favorite
                    this.code = favoriteMovie.code
                    it.insertOrUpdate(this)

                }
            }

    }

    //메인에서의 즐겨찾기 해제
    fun deleteFavorite(favoriteMovie: MovieData) {
        //메인 검색 결과 즐겨찾기 상태 변경
        movieSearchResult.forEach {
            if (it.code == favoriteMovie.code) {
                it.favorite = favoriteMovie.favorite
            }
        }
        searchResult.value = movieSearchResult

        //realm에 저장된 정보 삭제
        realm.executeTransactionAsync {
            it.where<Movie>().equalTo("id", favoriteMovie.code).findAll().deleteAllFromRealm()
        }

    }


    //즐겨찾기 화면에서의 즐겨찾기 삭제
    fun deleteFavorite(favoriteMovie: Movie) {
        //realm에 저장된 정보 삭제
        realm.executeTransaction {
            it.where<Movie>().equalTo("id", favoriteMovie.code).findAll().deleteAllFromRealm()
            favoriteList.value = getModelList()
        }

        //메인 검색 결과 즐겨찾기 상태 변경
        movieSearchResult.forEach {
            if (it.code == favoriteMovie.code) {
                it.favorite = !favoriteMovie.favorite
            }
        }
        searchResult.value = movieSearchResult

    }

    //특정 코드 찾기
    fun thisCodeMovie(code: Int): Boolean {
        var check = false
        realm.executeTransaction{
            val s = it.where<Movie>().equalTo("id", code).findFirst()
            check = s != null
        }
        return check
    }


    /**
     *검색어를 받아서 Naver API영화 검색 결과 받아오는 메소드
     *
     *@paramStringsearchKeyword :검색창에 있는 검색 키워드 값
     */
    fun getSearchData(searchKeyword: String) {
        val call: Call<MoviesData> = RetrofitClient.service.requestMovieJson(
            CLIENT_ID,
            CLIENT_SECRET,
            "movie.json",
            searchKeyword
        )
        call.enqueue(object : Callback<MoviesData> {
            override fun onResponse(call: Call<MoviesData>, response: Response<MoviesData>) {
                if (response.isSuccessful) { //response.code ==200
                    val data: MoviesData? = response.body()
                    if (data != null) {
                        Log.d("영화 데이터", data.items.size.toString())
                        movieSearchResult.clear()
                        data.items.forEach {

                            //영화 제목 태그 제거
                            val removeTag = "<b>"
                            var clearTitle = ""
                            var clearDirector = ""
                            var clearActor = ""
                            if (it.title.contains(removeTag)) {
                                clearTitle = it.title.replace(removeTag, "")
                                clearTitle = clearTitle.replace("</b>", "")
                            } else {
                                clearTitle = it.title
                            }
                            //감독
                            if (it.director.isNotEmpty()) {
                                clearDirector = it.director.replace("|", ", ")
                                clearDirector = clearDirector.substring(0, clearDirector.length - 2)
                            }
                            //배우
                            if (it.actor.isNotEmpty()) {
                                clearActor = it.actor.replace("|", ", ")
                                clearActor = clearActor.substring(0, clearActor.length - 2)
                            }
                            //코드 추출
                            val startIndex = it.link.indexOf("=")
                            val code = it.link.substring(startIndex + 1).toInt()
                            Log.d("코드추출 확인", code.toString())


                            val movieItem = MovieData(
                                clearTitle,
                                it.link,
                                it.image,
                                it.subtitle,
                                it.pubDate,
                                clearDirector,
                                clearActor,
                                it.userRating,
                                thisCodeMovie(code),//즐겨찾기에 있는 영화 정보인지 확인
                                code
                            )
                            movieSearchResult.add(movieItem)
                        }
                        searchResult.postValue(movieSearchResult)
                    }

                } else {
                    Log.e("Network", response.code().toString())
                }//response.code == 400
            }

            override fun onFailure(call: Call<MoviesData>, t: Throwable) { //response.code == 500

            }
        })
    }
}