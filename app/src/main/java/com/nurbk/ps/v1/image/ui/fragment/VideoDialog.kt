package com.nurbk.ps.v1.image.ui.fragment


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nurbk.ps.v1.image.R
import com.nurbk.ps.v1.image.adapter.VideoAdapter
import com.nurbk.ps.v1.image.model1.modelVideo.Video
import com.nurbk.ps.v1.image.ui.activty.MainActivity
import com.nurbk.ps.v1.image.ui.viewModel.ImagesViewModel
import com.nurbk.ps.v1.image.util.OnScrollListener
import com.nurbk.ps.v1.image.util.Resource
import kotlinx.android.synthetic.main.dialog_video.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.menu_sheet.*
import timber.log.Timber


class VideoDialog : Fragment(R.layout.dialog_video), VideoAdapter.OnClickMenuSheet {

    private val TAG = "VideoDialog"
    private lateinit var viewModel: ImagesViewModel

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    private val videoAdapter by lazy {
        VideoAdapter(this, requireActivity())
    }

    private lateinit var data: Video

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ImagesViewModel::class.java]
        data = requireArguments().getSerializable("video") as Video
        val search = data.url.split("/")[4].split("-")

        Log.e("ttttt", "$search")
        viewModel.getSearchVideo(search[1])
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        andExoPlayerView.setSource("${data.video_files[data.video_files.size - 1].link}.m3u8")





        setUpRecyclerView()

        viewModel.searchVideo.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { videoResponse ->
                        videoAdapter.differ.submitList(videoResponse.videos)
                        val totalPage = videoResponse.total_results / 15
                        isLastPage = viewModel.searchImagePage == totalPage
                        if (isLastPage) {
                            rcVideoUsers.setPadding(0, 0, 0, 0)
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
            (requireActivity() as MainActivity).bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN
        }
        requireActivity().btnSend.setOnClickListener {
            Timber.d("$TAG onViewCreated->   btnSend.setOnClickListener")
//            val photo = videoAdapter.getVideoItemAt(position)
            (requireActivity() as MainActivity).bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN
        }
        requireActivity().btnDownload.setOnClickListener {
            Timber.d("$TAG onViewCreated-> btnDownload.setOnClickListener")

            (requireActivity() as MainActivity).downloadImage(data.video_files[0].link)
            (requireActivity() as MainActivity).bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setUpRecyclerView() {
        Timber.d("$TAG setUpRecyclerView")
        rcVideoUsers.apply {
            adapter = videoAdapter
            layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            addOnScrollListener(OnScrollListener(isLoading, isLastPage) {
                viewModel.getResponseVideo()
                isScrolling = false
            })
        }
    }

    private fun hideProgressBar() {

//        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {

//        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true

    }

    override fun onClickItemListener(video: Video) {
        (requireActivity() as MainActivity).bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_EXPANDED
        this.data = video
    }

    override fun onClickItemListener(data: Video, i: Int) {
        if (this.data.id != data.id) {
            andExoPlayerView.stopPlayer()
            andExoPlayerView.setSource("${data.video_files[data.video_files.size - 1].link}.m3u8")
        }
    }
}