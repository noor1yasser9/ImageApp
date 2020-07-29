package com.nurbk.ps.v1.image.network


import com.nurbk.ps.v1.image.model1.modelVideo.ItemVideo
import com.nurbk.ps.v1.image.util.Constants.API_KEY_VIDEO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface VideoApi {

    @Headers(API_KEY_VIDEO)
    @GET("videos/popular")
    suspend fun getVideo(
        @Query("page") page: Int = 3
    ): Response<ItemVideo>

    @Headers(API_KEY_VIDEO)
    @GET("videos/search")
    suspend fun searchForVideo(
        @Query("query")
        query: String="nature",
        @Query("page")
        page: Int = 1
    ): Response<ItemVideo>
}