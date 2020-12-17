package com.nurbk.ps.v1.image.adapter


import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nurbk.ps.v1.image.R
import com.nurbk.ps.v1.image.databinding.ItemPhotoBinding
import com.nurbk.ps.v1.image.model1.ImageListItem
import com.nurbk.ps.v1.image.model1.ImageSave
import com.nurbk.ps.v1.image.util.Constants.loadImage


class SaveImageAdapter(
    val onClick: OnClickMenuSheet,
    val activity: Activity,
    var dataSource: ArrayList<ImageSave>
) :
    RecyclerView.Adapter<SaveImageAdapter.ImageHolder>() {

    inner class ImageHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)


    fun getImageItemAt(position: Int): ImageSave {
        return dataSource[position]
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_photo, parent, false
            )
        )
    }

    override fun getItemCount() =dataSource.size


    override fun onBindViewHolder(holder: ImageHolder, position: Int) {

        val photo = getImageItemAt(position)

        val h = photo.height!!.toFloat()

        holder.binding.apply {

            val layoutParams = this.containerPhoto.layoutParams
            layoutParams.height = convertDpToPixels(h / 17, activity)
            this.containerPhoto.layoutParams = layoutParams

            loadImage(
                activity,
                photo.urls!!.regular!!,
                Color.parseColor(photo.color)
                , this.containerPhoto
            )


//            txtPhoto.text = photo.description


            this.containerPhoto.setOnClickListener {
                onClick.onClickItemListener(photo, containerPhoto, position)

            }
            this.btnMenuSheet.setOnClickListener {
                onClick.onClickItemListener(position)
            }


        }

    }

    interface OnClickMenuSheet {
        fun onClickItemListener(position: Int)
        fun onClickItemListener(data: ImageSave, image: ImageView, position: Int)
    }

    private fun convertDpToPixels(dp: Float, context: Context): Int {
        val resources: Resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }


}