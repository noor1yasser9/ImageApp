package com.nurbk.ps.v1.image.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nurbk.ps.v1.image.R
import com.nurbk.ps.v1.image.adapter.ImageAdapter
import com.nurbk.ps.v1.image.adapter.SaveImageAdapter
import com.nurbk.ps.v1.image.model1.ImageListItem
import com.nurbk.ps.v1.image.model1.ImageSave
import com.nurbk.ps.v1.image.ui.viewModel.ImagesViewModel
import com.nurbk.ps.v1.image.util.OnScrollListener
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_images.*
import timber.log.Timber


class FavoriteFragment : Fragment(R.layout.fragment_favorite), SaveImageAdapter.OnClickMenuSheet {

    private val TAG = "FavoriteFragment"
    private val imageAdapter by lazy {
        SaveImageAdapter(this, requireActivity(), ArrayList())
    }

    private lateinit var viewModel: ImagesViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ImagesViewModel::class.java]


        setUpRecyclerView()

        viewModel.getImageSave().observe(viewLifecycleOwner, Observer {
            imageAdapter.dataSource.clear()
            imageAdapter.dataSource = it as ArrayList<ImageSave>
            imageAdapter.notifyDataSetChanged()
        })

    }


    private fun setUpRecyclerView() {
        Timber.d("$TAG setUpRecyclerView")
        rcSaveImage.apply {
            adapter = imageAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }


    override fun onClickItemListener(position: Int) {

    }

    override fun onClickItemListener(data: ImageSave, image: ImageView, position: Int) {
    }
}