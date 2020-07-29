package com.nurbk.ps.v1.image.model1.modelImage

import com.nurbk.ps.v1.image.model1.ImageListItem

class ImageSearch(
    val total: Int,
    val total_pages: Int,
    val results: ArrayList<ImageListItem>
) {
}