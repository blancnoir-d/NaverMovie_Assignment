package me.saeha.android.navermovie_project.network


import me.saeha.android.navermovie_project.model.NaverMoviesData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitInterface {
    //String 형태(데이터 확인용)
    @GET("search/{type}")
    fun requestMovieString(
        @Header("X-Naver-Client-Id")id: String,
        @Header("X-Naver-Client-Secret") pw: String,
        @Path("type") type: String,
        @Query("query") query: String?,
    ): Call<String>

    //NaverAPI에서 받아온 Json데이터 Model로 파싱
    @GET("search/{type}")
    fun requestMovieJson(
        @Header("X-Naver-Client-Id")id: String,
        @Header("X-Naver-Client-Secret") pw: String,
        @Path("type") type: String,
        @Query("query") query: String?,
        @Query("display") display: String?,
    ): Call<NaverMoviesData>
}