package com.nurbk.ps.v1.image.ui.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nurbk.ps.v1.image.R
import com.nurbk.ps.v1.image.adapter.VideoAdapter
import com.nurbk.ps.v1.image.databinding.FragmentVideoBinding
import com.nurbk.ps.v1.image.model1.modelVideo.Video
import com.nurbk.ps.v1.image.ui.activty.MainActivity
import com.nurbk.ps.v1.image.ui.viewModel.ImagesViewModel
import com.nurbk.ps.v1.image.util.OnScrollListener
import com.nurbk.ps.v1.image.util.Resource
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.menu_sheet.*
import timber.log.Timber

class VideoFragment : Fragment(), VideoAdapter.OnClickMenuSheet {

    private val TAG = "VideoFragment"
    private lateinit var viewModel: ImagesViewModel
    lateinit var mBinding: FragmentVideoBinding
    private lateinit var options: ActivityOptionsCompat


    private val videoAdapter by lazy {
        VideoAdapter(this, requireActivity())
    }


    private var position = -1
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    private lateinit var video: Video
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentVideoBinding.inflate(inflater, container, false)

        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ImagesViewModel::class.java]
        options = ActivityOptionsCompat
            .makeSceneTransitionAnimation(requireActivity(), view, "")

        Timber.d("$TAG onViewCreated")

        setUpRecyclerView()


        viewModel.videoLiveData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { imageResponse ->
                        videoAdapter.differ.submitList(imageResponse.videos)
                        val totalPage = imageResponse.total_results / 1
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
        requireActivity().btnSend.setOnClickListener {
            Timber.d("$TAG onViewCreated->   btnSend.setOnClickListener")
            val photo = videoAdapter.getVideoItemAt(position)
            (requireActivity() as MainActivity).bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN
        }
        requireActivity().btnDownload.setOnClickListener {
            Timber.d("$TAG onViewCreated-> btnDownload.setOnClickListener")

            (requireActivity() as MainActivity).downloadImage(video.video_files[0].link)
            (requireActivity() as MainActivity).bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN
        }


    }

    private fun setUpRecyclerView() {
        Timber.d("$TAG setUpRecyclerView")
        rcVideo.apply {
            adapter = videoAdapter
            layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            addOnScrollListener(OnScrollListener(isLoading, isLastPage) {
                viewModel.getResponseVideo()
                isScrolling = false
            })
        }
    }


    override fun onClickItemListener(video: Video) {
        (requireActivity() as MainActivity).bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_EXPANDED
        this.video = video
    }

    override fun onClickItemListener(data: Video, i: Int) {
        openCreateVideoDialog(data)
    }

    private fun hideProgressBar() {

//        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {

//        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true

    }


    private fun openCreateVideoDialog(video: Video) {
        val bundle = Bundle()
        bundle.putSerializable("video", video)

        findNavController().navigate(R.id.action_videoFragment_to_videoDialog2, bundle)
    }




}