package com.nurbk.ps.v1.image.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.nurbk.ps.v1.image.databinding.FragmentDetailsImageBinding
import com.nurbk.ps.v1.image.model1.ImageListItem
import com.nurbk.ps.v1.image.ui.viewModel.ImagesViewModel
import com.nurbk.ps.v1.image.util.Constants

class ImageDetailsFragment : Fragment() {

    private lateinit var mBinding: FragmentDetailsImageBinding
    private val args: ImageDetailsFragmentArgs by navArgs()

    private lateinit var viewModel: ImagesViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentDetailsImageBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        viewModel = ViewModelProvider(requireActivity())[ImagesViewModel::class.java]



        val photo = args.ImageDetails
        mBinding.executePendingBindings()

        Constants.loadImage(
            requireActivity(),
            photo .urls!!.regular!!,
            Color.parseColor(photo.color)
            , mBinding.imageDetails
        )
        Constants.loadImage(
            requireActivity(),
            photo.user!!.profile_image.medium!!,
            Color.parseColor(photo.color)
            , mBinding.imageUsersDetails
        )

        mBinding.nameUsersDetails.text = photo.user.name
        mBinding.allLikeDetails.text = photo.user.total_likes.toString()
        if (photo.user.bio != null) {
            mBinding.txtDesDetails.visibility = View.VISIBLE
            mBinding.txtDesDetails.text =photo.user.bio.toString()
        }

    }
}