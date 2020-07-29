package com.nurbk.ps.v1.image.model1.modelVideo

data class ItemVideo(
    val page: Int,
    val per_page: Int,
    val total_results: Int,
    val url: String,
    val videos: ArrayList<Video>
)