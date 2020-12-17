package com.nurbk.ps.v1.image.network


import com.nurbk.ps.v1.image.model1.modelImage.ImageList
import com.nurbk.ps.v1.image.model1.modelImage.ImageSearch
import com.nurbk.ps.v1.image.util.Constants.API_KEY_PHOTO
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface ImageApi {


    @Headers(API_KEY_PHOTO)
    @GET("photos")
    suspend fun getPhoto(
        @Query("page") page: Int=1
    ): Response<ImageList>


    @Headers(API_KEY_PHOTO)
    @GET("search/photos")
    suspend fun searchForImage(
        @Query("page")
        page: Int=1,
        @Query("query")
        query: String
        ): Response<ImageSearch>

}