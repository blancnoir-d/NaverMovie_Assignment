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
import me.saeha.android.navermovie_project.model.NaverMoviesData
import me.saeha.android.navermovie_project.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {

    //영화 검색 결과
    var searchResult = MutableLiveData<MutableList<MovieData>>()
    val liveSearchResult: LiveData<MutableList<MovieData>>
        get() = searchResult

    //영화 검색 결과 원본
    var movieSearchResult: MutableList<MovieData> = arrayListOf()

    //즐겨찾기 목록
    private var favoriteList = MutableLiveData<MutableList<Movie>>()
    val liveFavoriteList: LiveData<MutableList<Movie>>
        get() = favoriteList

    //Naver Key
    private val CLIENT_ID = "6GXwvLWETQz9pjFapwto"
    private val CLIENT_SECRET = "YC92nIro4P"

    //Realm
    var realm: Realm
    val pagingSize = 5

    init {
        searchResult.value = movieSearchResult
        realm = Realm.getDefaultInstance()
        favoriteList.value = getModelList()
    }

    /**
     *검색 결과 보여주는 RecyclerView에서 바닥인지 이벤트가 일어났을 때 다음 영화 목록을 불러오기 위한 메소드(paging)
     *
     * @param resultSize : 현재 검색 결과 리스트 크기
     */
    fun getNextPage(resultSize: Int){
        //원본 크기보다 작으면
        if(resultSize < movieSearchResult.size){
            if(movieSearchResult.size - resultSize >= pagingSize){
                searchResult.postValue(movieSearchResult.subList(0,resultSize+pagingSize))
            }else{
                searchResult.postValue(movieSearchResult.subList(0,movieSearchResult.size))
            }
        }
    }

    /**
     *즐겨찾기가 업데이트 되면 기존에 있는 검색 결과 List에도 변경된 정보 반영해주기 위한 메소드
     */
    fun updateResultListFavorite(){
        favoriteList.value = getModelList()
        for(item in favoriteList.value!!){
            for(j in searchResult.value!!){
                j.favorite = item.code == j.code
            }
        }
    }


    /**
     *DBdp 저장된 즐겨찾기 정보 가져오기
     */
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


    /**
     *메인 화면에서 즐겨찾기 추가했을 시 DB에 있는 데이터도 추가하기 위한 메소드
     *
     *@param favoriteMovie :즐겨찾기 한 영화 객체
     */
    fun addFavorite(favoriteMovie: MovieData) {

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


    /**
     *메인 화면에서 즐겨찾기 해제했을 때 검색 결과 리스트의 정보 변경과
     *DB에 있는 데이터도 삭제하기 위한 메소드
     *
     *@param favoriteMovie :즐겨찾기 해제한 영화 객체
     */

    fun deleteFavorite(favoriteMovie: MovieData) {
        //메인 검색 결과 즐겨찾기 상태 변경
        movieSearchResult.forEach {
            if (it.code == favoriteMovie.code) {
                it.favorite = favoriteMovie.favorite
                Log.d("MainActivity_즐겨찾기 값",it.favorite.toString())
            }
        }

        //realm에 저장된 정보 삭제
        realm.executeTransactionAsync {
            it.where<Movie>().equalTo("id", favoriteMovie.code).findAll().deleteAllFromRealm()
        }
    }

    /**
     *즐겨찾기 화면에서 즐겨찾기 한 영화 삭제
     *
     *@param favoriteMovie :즐겨찾기 해제한 영화 객체
     */
    fun deleteFavorite(favoriteMovie: Movie) {
        //realm에 저장된 정보 삭제
        realm.executeTransaction {
            it.where<Movie>().equalTo("id", favoriteMovie.code).findAll().deleteAllFromRealm()
            favoriteList.value = getModelList()

            Log.d("즐찾 삭제시 확인",searchResult.value?.size.toString())
        }
    }

    /**
     *즐겨찾기에 있는 영화인지 아닌지를 알기 위한 특정 영화 코드 찾기 메소드
     *
     *@param code :영화 link값 끝에 있는 영화 고유 코드
     */
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
     *@param StringsearchKeyword :검색창에 있는 검색 키워드 값
     */
    fun getSearchData(searchKeyword: String) {
        val call: Call<NaverMoviesData> = RetrofitClient.service.requestMovieJson(
            CLIENT_ID,
            CLIENT_SECRET,
            "movie.json",
            searchKeyword,
            "30" //30개씩 가져오기
        )
        call.enqueue(object : Callback<NaverMoviesData> {
            override fun onResponse(call: Call<NaverMoviesData>, response: Response<NaverMoviesData>) {
                if (response.isSuccessful) { //response.code ==200
                    val data: NaverMoviesData? = response.body()
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

                        //paging을 위해서 여기서 sublist (5개씩 보여주기)
                        if(movieSearchResult.size/pagingSize < 1){ //paging 로드하려는 개수보다 전체 데이터가 작을 때
                            searchResult.postValue(movieSearchResult)
                        }else{
                            searchResult.postValue(movieSearchResult.subList(0,pagingSize))
                        }
                    }

                } else {
                    Log.e("Network", response.code().toString())
                }//response.code == 400
            }

            override fun onFailure(call: Call<NaverMoviesData>, t: Throwable) { //response.code == 500

            }
        })
    }
}