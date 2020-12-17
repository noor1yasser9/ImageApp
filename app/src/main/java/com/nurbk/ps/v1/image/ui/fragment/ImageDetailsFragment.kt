package com.nurbk.ps.v1.image.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.nurbk.ps.v1.image.R
import com.nurbk.ps.v1.image.adapter.ImageAdapter
import com.nurbk.ps.v1.image.adapter.SliderAdapter
import com.nurbk.ps.v1.image.databinding.FragmentDetailsImageBinding
import com.nurbk.ps.v1.image.model1.ImageListItem
import com.nurbk.ps.v1.image.model1.ImageSave
import com.nurbk.ps.v1.image.ui.viewModel.ImagesViewModel
import com.nurbk.ps.v1.image.util.Constants
import com.nurbk.ps.v1.image.util.Resource
import kotlinx.android.synthetic.main.fragment_details_image.*
import kotlinx.android.synthetic.main.fragment_details_image.viewPager
import kotlinx.android.synthetic.main.fragment_search.*
import timber.log.Timber
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.collections.ArrayList

class ImageDetailsFragment : Fragment(),
    ImageAdapter.OnClickMenuSheet, SliderAdapter.OnClickSlider {
    private val TAG = "ImageDetailsFragment"
    private lateinit var mBinding: FragmentDetailsImageBinding

    private lateinit var viewModel: ImagesViewModel

    private var position: Int? = null
    private lateinit var imageAdapter: SliderAdapter
    private lateinit var bundle: Bundle
    private var isLoading = false
    private var isLastPage = false
    private lateinit var dataSearch: String
    private lateinit var adapterImage: ImageAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentDetailsImageBinding.inflate(inflater, container, false)
        bundle = requireArguments()

        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        viewModel = ViewModelProvider(requireActivity())[ImagesViewModel::class.java]

        position = bundle.getInt("position")


        val data = bundle.getParcelableArrayList<ImageListItem>("images")
        adapterImage = ImageAdapter(this, requireActivity(), ArrayList())
        imageAdapter = SliderAdapter(adapterImage, this)
        imageAdapter.differ.submitList(data)
        clearData()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                clearData()

                try {
                    dataSearch = data!![position].alt_description?.split(" ")!![0]
                    Log.e("tttstatus", "${data[position].status}")
                    viewModel.getSearchImage(dataSearch)
                } catch (e: KotlinNullPointerException) {
                    e.printStackTrace()
                    Log.e("ttttt", "${e.message}")
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        })
        viewPager.adapter = imageAdapter
        viewPager.setCurrentItem(position!!, false)


        viewModel.searchImage.observe(requireActivity(), Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { imageResponse ->

                        adapterImage.dataSource = imageResponse.results
                        adapterImage.notifyDataSetChanged()

                        dataSource.clear()
                        dataSource.addAll(imageResponse.results)
                        val totalPage = imageResponse.total_pages / 7
                        isLastPage = viewModel.searchImagePage == totalPage
                        if (isLastPage) {
                            rcSearchImage.setPadding(0, 0, 0, 0)
                        }

                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Timber.d("$TAG onViewCreated->response.message->$message")
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })


    }


    private fun hideProgressBar() {

//        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {

//        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true

    }


    override fun onClickItemListener(position: Int) {


    }

    val dataSource = ArrayList<ImageListItem>()

    override fun onClickItemListener(data: ImageListItem, image: ImageView, position: Int) {
        clearData()
        val bundle = Bundle()
        bundle.putInt("position", position)
        bundle.putParcelableArrayList(
            "images",
            dataSource
        )

        findNavController().navigate(R.id.action_imageDetailsFragment_self, bundle)
    }

    override fun onClickItemListenerSlider(image: ImageListItem) {


        var isSave = true
        viewModel.getImageSave().observe(viewLifecycleOwner, Observer {
            for (imageSave in it) {
                if (image.id == imageSave.id) {
                    isSave = false
                    break
                }
            }

            if (isSave)
                viewModel.saveImage(
                    ImageSave(
                        image.alt_description, image.color,
                        image.created_at, image.description, image.height
                        , image.id, image.liked_by_user, image.likes,
                        image.links, image.promoted_at, image.updated_at, image.urls,
                        image.user, image.width, 0
                    )
                )

        })

    }


    fun clearData() {
        if (adapterImage.dataSource.isNotEmpty()) {
            adapterImage.dataSource.clear()
        }

    }
}