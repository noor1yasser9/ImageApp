package com.nurbk.ps.v1.image.db

import  androidx.lifecycle.LiveData
import androidx.room.*
import com.nurbk.ps.v1.image.model1.ImageListItem
import com.nurbk.ps.v1.image.model1.ImageSave

@Dao
interface ImageDao {


    @Insert
    suspend fun insert(images: List<ImageListItem>)

    @Query("SELECT * FROM ImageListItem")
    fun getAllImage(): LiveData<List<ImageListItem>>

    @Query("DELETE FROM ImageListItem")
    fun deleteAll()

    @Insert
    suspend fun saveImage(image: ImageSave)

    @Query("SELECT * FROM ImageSave")
    fun getImageSave(): LiveData<List<ImageSave>>

}