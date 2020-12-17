package com.nurbk.ps.v1.image.ui.fragment

import android.os.Bundle
import android.os.Parcelable
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nurbk.ps.v1.image.MyApplication.Companion.hasNetwork
import com.nurbk.ps.v1.image.R
import com.nurbk.ps.v1.image.adapter.ImageAdapter
import com.nurbk.ps.v1.image.databinding.FragmentImagesBinding
import com.nurbk.ps.v1.image.model1.ImageListItem
import com.nurbk.ps.v1.image.ui.activty.MainActivity
import com.nurbk.ps.v1.image.ui.viewModel.ImagesViewModel
import com.nurbk.ps.v1.image.util.Constants.QUERY_PAGE_SIZE
import com.nurbk.ps.v1.image.util.OnScrollListener
import com.nurbk.ps.v1.image.util.Resource
import kotlinx.android.synthetic.main.fragment_details_image.*
import kotlinx.android.synthetic.main.fragment_images.*
import kotlinx.android.synthetic.main.menu_sheet.*
import timber.log.Timber


class ImagesFragment : Fragment(), ImageAdapter.OnClickMenuSheet {

    private val TAG = "ImagesFragment"
    private lateinit var viewModel: ImagesViewModel

    //    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var mBinding: FragmentImagesBinding
    private lateinit var options: ActivityOptionsCompat

    private var position = -1
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    private val dataSource = ArrayList<ImageListItem>()
    private val imageAdapter by lazy {
        ImageAdapter(this, requireActivity(), dataSource)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentImagesBinding.inflate(inflater, container, false)

        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)


        return mBinding.root
    }

    var imagsLiset = ArrayList<ImageListItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ImagesViewModel::class.java]
        options = ActivityOptionsCompat
            .makeSceneTransitionAnimation(requireActivity(), view, "")


        Timber.d("$TAG onViewCreated")



        setUpRecyclerView()

        if (hasNetwork()) {
            viewModel.imageLiveData.observe(viewLifecycleOwner, Observer { response ->
                Timber.d("$TAG onViewCreated->viewModel")
                when (response) {
                    is Resource.Success -> {
                        Timber.d("$TAG onViewCreated->Resource.Success")
                        hideProgressBar()
                        response.data?.let { imageResponse ->
                            Timber.d("$TAG onViewCreated->Resource.Success->response.data")
//                            imageAdapter.dataSource.addAll(imageResponse.toList())
                            dataSource.addAll(imageResponse)
                            imageAdapter.notifyDataSetChanged()
                            val totalPages = imageResponse.totalPage / QUERY_PAGE_SIZE + 2
                            isLastPage = viewModel.imagePage == totalPages
                            if (isLastPage) {
                                rvPhoto.setPadding(0, 0, 0, 0)
                            }

                            imagsLiset.clear()
                            imagsLiset.addAll(imageResponse)
                            viewModel.deleteAll()
                            viewModel.insert(imagsLiset)
                        }
                    }
                    is Resource.Error -> {
                        Timber.d("$TAG onViewCreated->Resource.Error")

                        hideProgressBar()
                        response.message?.let { message ->
                            Timber.d("$TAG onViewCreated->response.message->$message")
                            Toast.makeText(
                                activity,
                                "An error occured: $message",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    is Resource.Loading -> {
                        Timber.d("$TAG onViewCreated-> Resource.Loading")
                        showProgressBar()
                    }
                }

            })
        } else
            viewModel.getAllImage().observe(viewLifecycleOwner, Observer {
//                imageAdapter.dataSource.addAll(it)
                dataSource.addAll(it)
                imageAdapter.notifyDataSetChanged()
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

        val bundle = Bundle()


//        val data = imageAdapter.differ.currentList

        bundle.putInt("position", position)
        bundle.putParcelableArrayList(
            "images",
            imagsLiset
        )

        findNavController().navigate(R.id.action_imagesFragment_to_imageDetailsFragment, bundle)
//        val action =
//            ImagesFragmentDirections.actionImagesFragmentToImageDetailsFragment(data)
//
//
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
        Timber.d("$TAG hideProgressBar")

//        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        Timber.d("$TAG showProgressBar")

//        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setUpRecyclerView() {
        Timber.d("$TAG setUpRecyclerView")
        rvPhoto.apply {
            adapter = imageAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            addOnScrollListener(OnScrollListener(isLoading, isLastPage) {
                viewModel.getResponseImages()
                isScrolling = false
            })
        }
    }


}