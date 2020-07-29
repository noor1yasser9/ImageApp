package com.nurbk.ps.v1.image.model1.modelImage

data class User(
    val accepted_tos: Boolean,
    val bio: String?,
    val first_name: String?,
    val id: String,
    val instagram_username: String,
    val last_name: String,
    val name: String?,
    val profile_image: ProfileImageX,
    val total_likes: Int?,
    val twitter_username: Any,
    val username: String
)