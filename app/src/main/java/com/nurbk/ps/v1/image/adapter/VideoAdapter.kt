package com.nurbk.ps.v1.image.adapter


import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nurbk.ps.v1.image.R
import com.nurbk.ps.v1.image.databinding.ItemVideoBinding
import com.nurbk.ps.v1.image.model1.modelVideo.Video
import com.nurbk.ps.v1.image.util.Constants


class VideoAdapter(val onClick: OnClickMenuSheet, val activity: Activity) :
    RecyclerView.Adapter<VideoAdapter.ImageHolder>() {

    inner class ImageHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)


    private val differCallback = object : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(
            oldItem: Video,
            newItem: Video
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Video,
            newItem: Video
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun getVideoItemAt(position: Int): Video {
        return differ.currentList[position]
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_video, parent, false
            )
        )
    }

    override fun getItemCount() = differ.currentList.size


    override fun onBindViewHolder(holder: ImageHolder, position: Int) {

        val video = getVideoItemAt(position)

        val h = video.height.toFloat()

        holder.binding.apply {
            val layoutParams = this.card.layoutParams
            layoutParams.height = convertDpToPixels(h / 8, activity)
            this.card.layoutParams = layoutParams


            Constants.loadImage(
                activity,
                video.image,
                R.color.bgVideo
                , previewImageView
            )

            previewImageView.setOnClickListener {
                onClick.onClickItemListener(video,1)

            }
            btnVideo.setOnClickListener {
                onClick.onClickItemListener(video)
            }
        }

    }

    interface OnClickMenuSheet {
        fun onClickItemListener(video: Video)
        fun onClickItemListener(data: Video,i:Int)
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