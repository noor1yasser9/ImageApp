package com.nurbk.ps.v1.image.repository

import com.nurbk.ps.v1.image.db.ImagesDatabase
import com.nurbk.ps.v1.image.model1.ImageListItem
import com.nurbk.ps.v1.image.model1.ImageSave
import com.nurbk.ps.v1.image.network.RetrofitInstance

class ImageRepository(val db: ImagesDatabase) {

    suspend fun getPhoto(pageNumber: Int) = RetrofitInstance.apiPhoto!!.getPhoto(pageNumber)

    suspend fun searchForImage(
        page: Int,
        query: String
    ) = RetrofitInstance.apiPhoto!!.searchForImage(page, query)

    suspend fun getVideo(pageNumber: Int) = RetrofitInstance.apiVideo!!.getVideo(pageNumber)

    suspend fun searchForVideo(query: String, page: Int) =
        RetrofitInstance.apiVideo!!.searchForVideo(query, page)


    suspend fun insert(image: List<ImageListItem>) =
        db.getArticleDao().insert(image)


    fun getAllImage() = db.getArticleDao().getAllImage()

    fun deleteAll() = db.getArticleDao().deleteAll()

    suspend fun saveImage(image: ImageSave) = db.getArticleDao().saveImage(image)
    fun getImageSave() = db.getArticleDao().getImageSave()
}