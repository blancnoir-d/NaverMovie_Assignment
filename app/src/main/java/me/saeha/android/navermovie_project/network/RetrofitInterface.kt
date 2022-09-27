package me.saeha.android.navermovie_project.network


import me.saeha.android.navermovie_project.model.MoviesData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitInterface {
    //String 형태
    @GET("search/{type}")
    fun requestMovieString(
        @Header("X-Naver-Client-Id")id: String,
        @Header("X-Naver-Client-Secret") pw: String,
        @Path("type") type: String,
        @Query("query") query: String?,
    ): Call<String>

    //Model을 이용한 파싱
    @GET("search/{type}")
    fun requestMovieJson(
        @Header("X-Naver-Client-Id")id: String,
        @Header("X-Naver-Client-Secret") pw: String,
        @Path("type") type: String,
        @Query("query") query: String?,
    ): Call<MoviesData>

//    //RxJava 적용
//    @GET("search/{type}")
//    fun requestMovieRxJava(
//        @Header("X-Naver-Client-Id")id: String,
//        @Header("X-Naver-Client-Secret") pw: String,
//        @Path("type") type: String,
//        @Query("query") query: String?,
//    ): Observable<MoviesData>

}