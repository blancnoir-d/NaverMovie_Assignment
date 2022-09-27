package me.saeha.android.navermovie_project

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.saeha.android.navermovie_project.model.MovieData
import me.saeha.android.navermovie_project.model.MoviesData
import me.saeha.android.navermovie_project.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {
    //영화 검색 결과
    private var searchResult = MutableLiveData<ArrayList<MovieData>>()
    val liveSearchResult: LiveData<ArrayList<MovieData>>
        get() = searchResult

    private var newsItem: ArrayList<MovieData> = arrayListOf()

    //즐겨찾기 목록
    private var favoriteList = MutableLiveData<List<MovieData>>()
    val liveFavoriteList: LiveData<List<MovieData>>
    get() = favoriteList

    private val CLIENT_ID = "6GXwvLWETQz9pjFapwto"
    private val CLIENT_SECRET = "YC92nIro4P"

    init{
        searchResult.value = newsItem
    }

    /**
     * 검색어를 받아서 Naver API 영화 검색 결과 받아오는 메소드
     *
     * @param String searchKeyword : 검색창에 있는 검색 키워드 값
     */
    fun getSearchData(searchKeyword: String){
        val call: Call<MoviesData> = RetrofitClient.service.requestMovieJson(CLIENT_ID,CLIENT_SECRET,"movie.json",searchKeyword)
        call.enqueue(object: Callback<MoviesData> {
            override fun onResponse(call: Call<MoviesData>, response: Response<MoviesData>) {
                if(response.isSuccessful){ //response.code ==200
                    val data: MoviesData? = response.body()
                    if(data != null){
                        Log.d("영화 데이터", data.items.size.toString())

                        data.items.forEach {

                            //영화 제목 태그 제거
                            val removeTag = "<b>"
                            var clearTitle = ""
                            var clearDirector = ""
                            var clearActor = ""
                            if(it.title.contains(removeTag)) {
                                clearTitle = it.title.replace(removeTag, "")
                                clearTitle = clearTitle.replace("</b>", "")
                            }else{
                                clearTitle = it.title
                            }
                            //감독
                            clearDirector = it.director.replace("|",", ")
                            clearDirector = clearDirector.substring(0,clearDirector.length-2)

                            //배우
                            if(it.actor.isNotEmpty()){
                                clearActor = it.actor.replace("|",", ")
                                clearActor = clearActor.substring(0,clearActor.length-2)
                            }

                            val movieItem =MovieData(clearTitle, it.link, it.image, it.subtitle,it.pubDate,clearDirector,clearActor,it.userRating, false)
                            newsItem.add(movieItem)
                        }
                        searchResult.postValue(newsItem)
                    }

                }else{
                    Log.e("Network",response.code().toString())
                }//response.code == 400
            }

            override fun onFailure(call: Call<MoviesData>, t: Throwable) { //response.code == 500

            }
        })
    }
}