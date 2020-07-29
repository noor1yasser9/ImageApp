package com.nurbk.ps.v1.image.db

import  androidx.lifecycle.LiveData
import androidx.room.*
import com.nurbk.ps.v1.image.model1.ImageListItem

@Dao
interface ImageDao {


    @Insert
    suspend fun insert(images: List<ImageListItem>)

    @Query("SELECT * FROM ImageListItem")
    fun getAllImage(): LiveData<List<ImageListItem>>

    @Query("DELETE FROM ImageListItem")
    fun deleteAll()

}