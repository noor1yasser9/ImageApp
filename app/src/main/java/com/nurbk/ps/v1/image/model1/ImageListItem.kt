package com.nurbk.ps.v1.image.model1

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nurbk.ps.v1.image.model1.modelImage.*
import java.io.Serializable


@Entity(
    tableName = "ImageListItem"
)
data class ImageListItem(
    val alt_description: String?,
    val color: String?,
    val created_at: String?,
    val description: String?,
    val height: Int?,
    val id: String?,
    val liked_by_user: Boolean?,
    val likes: Int?,
    val links: Links?,
    val promoted_at: String?,
    val updated_at: String?,
    val urls: Urls?,
    val user: User?,
    val width: Int?
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var ids: Int? = 0

}