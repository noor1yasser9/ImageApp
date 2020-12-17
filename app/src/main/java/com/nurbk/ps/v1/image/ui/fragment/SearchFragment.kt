package com.nurbk.ps.v1.image.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nurbk.ps.v1.image.adapter.ImageAdapter
import com.nurbk.ps.v1.image.databinding.FragmentSearchBinding
import com.nurbk.ps.v1.image.model1.ImageListItem
import com.nurbk.ps.v1.image.model1.modelVideo.Video
import com.nurbk.ps.v1.image.ui.activty.MainActivity
import com.nurbk.ps.v1.image.ui.viewModel.ImagesViewModel
import com.nurbk.ps.v1.image.util.Constants.SEARCH_NEWS_TIME_DELAY
import com.nurbk.ps.v1.image.util.OnScrollListener
import com.nurbk.ps.v1.image.util.Resource
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.menu_sheet.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment(), ImageAdapter.OnClickMenuSheet {

    private val TAG = "SearchFragment"
    private lateinit var viewModel: ImagesViewModel
    lateinit var mBinding: FragmentSearchBinding
    private lateinit var options: ActivityOptionsCompat


    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    private var position = -1

    private val imageAdapter by lazy {
        ImageAdapter(this, requireActivity(), ArrayList())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSearchBinding.inflate(inflater, container, false)

        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        return mBinding.root
    }

    private var search = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ImagesViewModel::class.java]
        options = ActivityOptionsCompat
            .makeSceneTransitionAnimation(requireActivity(), view, "")

        Timber.d("$TAG onViewCreated")


        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.getSearchImage(editable.toString())
                        search = etSearch.text.toString()
                    }
                }
            }
        }
        setUpRecyclerView()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (imageAdapter.dataSource.isNotEmpty())
                    imageAdapter.dataSource.clear()

                viewModel.getSearchImage(etSearch.text.toString())

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        viewModel.searchImage.observe(requireActivity(), Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { imageResponse ->
//                        imageAdapter.differ.submitList(imageResponse.results)
                        imageAdapter.dataSource = imageResponse.results
                        imageAdapter.notifyDataSetChanged()
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


        requireActivity().btnSend.setOnClickListener {
            Timber.d("$TAG onViewCreated->   btnSend.setOnClickListener")
            val photo = imageAdapter.getImageItemAt(position)
            (requireActivity() as MainActivity).bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN
        }
        requireActivity().btnDownload.setOnClickListener {
            Timber.d("$TAG onViewCreated-> btnDownload.setOnClickListener")
            val photo = imageAdapter.getImageItemAt(position)
            (requireActivity() as MainActivity).downloadImage(photo.links!!.download)
            (requireActivity() as MainActivity).bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN
        }

    }

    private fun setUpRecyclerView() {
        Timber.d("$TAG setUpRecyclerView")
        rcSearchImage.apply {
            adapter = imageAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            addOnScrollListener(OnScrollListener(isLoading, isLastPage) {
                viewModel.getSearchImage(search)
                isScrolling = false
            })
        }
    }

    override fun onClickItemListener(position: Int) {

        Timber.d("$TAG  imageAdapter->onClickItemListener")
        this.position = position
        (requireActivity() as MainActivity).bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onClickItemListener(data: ImageListItem, image: ImageView, position: Int) {
        Timber.d("$TAG onViewCreated->  imageAdapter.setOnItemClickListener")
        (requireActivity() as MainActivity).bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_HIDDEN

//        val action = SearchFragmentDirections.actionSearchFragmentToImageDetailsFragment(data)

//        findNavController()
//            .navigate(
//                action,
//                FragmentNavigator.Extras.Builder()
//                    .addSharedElements(
//                        mapOf(image to image.transitionName)
//                    ).build()
//            )

    }


    private fun hideProgressBar() {

//        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {

//        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true

    }


}