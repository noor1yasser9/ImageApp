package com.nurbk.ps.v1.image.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nurbk.ps.v1.image.model1.ImageListItem
import com.nurbk.ps.v1.image.model1.ImageSave


@Database(
    entities = [ImageListItem::class, ImageSave::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ImagesDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ImageDao

    companion object {
        @Volatile
        private var instance: ImagesDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) =
            instance ?: synchronized(LOCK) {
                instance ?: createDatabase(context).also {
                    instance = it
                }
            }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ImagesDatabase::class.java,
                "PhotoV1"
            ).build()

    }
}