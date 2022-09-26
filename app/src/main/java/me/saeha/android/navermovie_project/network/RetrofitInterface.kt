package me.saeha.android.navermovie_project.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("search/{type}")
    fun requestMovie(
        @Header("X-Naver-Client-Id")id: String,
        @Header("X-Naver-Client-Secret") pw: String,
        @Path("type") type: String,
        @Query("query") query: String?,
    ): Call<String>
}