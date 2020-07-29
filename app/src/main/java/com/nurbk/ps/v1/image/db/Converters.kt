package com.nurbk.ps.v1.image.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nurbk.ps.v1.image.model1.modelImage.Links
import com.nurbk.ps.v1.image.model1.modelImage.Urls
import com.nurbk.ps.v1.image.model1.modelImage.User

class Converters {

    @TypeConverter
    fun gsonToLinks(json: String?): Links {
        return Gson().fromJson(json, Links::class.java)
    }

    @TypeConverter
    fun factLinksToGson(link: Links): String? {
        return Gson().toJson(link)
    }


    @TypeConverter
    fun gsonToUrls(json: String?): Urls {
        return Gson().fromJson(json, Urls::class.java)
    }

    @TypeConverter
    fun factUrlsToGson(urls: Urls): String? {
        return Gson().toJson(urls)
    }

    @TypeConverter
    fun gsonToUser(json: String?): User {
        return Gson().fromJson(json, User::class.java)
    }

    @TypeConverter
    fun factUserToGson(user: User): String? {
        return Gson().toJson(user)
    }
}