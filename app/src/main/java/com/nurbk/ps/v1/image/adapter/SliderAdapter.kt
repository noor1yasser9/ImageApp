package com.nurbk.ps.v1.image.adapter

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.*
import com.nurbk.ps.v1.image.R
import com.nurbk.ps.v1.image.model1.ImageListItem
import com.nurbk.ps.v1.image.util.Constants
import kotlinx.android.synthetic.main.item_silder_image.view.*

class SliderAdapter(val adapter: ImageAdapter, val onClick: OnClickSlider) :
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(item: View) : RecyclerView.ViewHolder(item)


    private val differCallback = object : DiffUtil.ItemCallback<ImageListItem>() {
        override fun areItemsTheSame(
            oldItem: ImageListItem,
            newItem: ImageListItem
        ): Boolean {
            return oldItem.id != newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ImageListItem,
            newItem: ImageListItem
        ): Boolean {
            return oldItem.hashCode() != newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    fun getImageItemAt(position: Int): ImageListItem {
        return differ.currentList[position]
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_silder_image, parent, false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val photo = getImageItemAt(position)
        holder.itemView.apply {

            Constants.loadImage(
                this.context,
                photo.urls!!.regular!!,
                Color.parseColor(photo.color)
                , this.imageDetails
            )


            this.rvPhotoSlider.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)


            this.rvPhotoSlider.setRecycledViewPool(RecyclerView.RecycledViewPool())
            this.rvPhotoSlider.recycledViewPool.clear()
            this.rvPhotoSlider.adapter = adapter

            btnSaveImageDetails.setOnClickListener {
                onClick.onClickItemListenerSlider(photo)
            }

        }

    }


    interface OnClickSlider {
        fun onClickItemListenerSlider(image: ImageListItem)
    }


}