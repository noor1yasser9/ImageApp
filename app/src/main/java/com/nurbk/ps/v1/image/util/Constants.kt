package com.nurbk.ps.v1.image.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_details_image.*

object Constants {

    const val API_KEY_PHOTO = "Authorization: Client-ID EcPQ4ajISM6oluDFxZDGCWxQHP2ovIkHdd4yLTFvVoU"
    const val BASE_URL_PHOTO = "https://api.unsplash.com"

    const val API_KEY_VIDEO = "Authorization: 563492ad6f91700001000001f33189254b334dd6be397f6d017ab277"
    const val BASE_URL_VIDEO = "https://api.pexels.com"

    const val SEARCH_NEWS_TIME_DELAY = 500L
    const val QUERY_PAGE_SIZE = 10



    fun loadImage(context: Context, url: String, placeholder: Int, image: ImageView) {
        Glide.with(context)
            .load(url)
            .placeholder(placeholder)
            .into(image)
    }
}