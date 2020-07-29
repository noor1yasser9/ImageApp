package com.nurbk.ps.v1.image.model1.modelVideo

import java.io.Serializable

data class Video(
    val duration: Int,
    val full_res: Any,
    val height: Int,
    val id: Int,
    val image: String,
    val tags: List<Any>,
    val url: String,
    val user: User,
    val video_files: List<VideoFile>,
    val video_pictures: List<VideoPicture>,
    val width: Int
):Serializable