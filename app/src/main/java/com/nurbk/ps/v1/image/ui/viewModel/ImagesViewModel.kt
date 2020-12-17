package com.nurbk.ps.v1.image.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nurbk.ps.v1.image.db.ImagesDatabase
import com.nurbk.ps.v1.image.model1.ImageListItem
import com.nurbk.ps.v1.image.model1.ImageSave
import com.nurbk.ps.v1.image.model1.modelImage.ImageList
import com.nurbk.ps.v1.image.model1.modelImage.ImageSearch
import com.nurbk.ps.v1.image.model1.modelVideo.ItemVideo
import com.nurbk.ps.v1.image.repository.ImageRepository
import com.nurbk.ps.v1.image.util.Resource
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

class ImagesViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "ImagesViewModel"
    private val imageRepository =
        ImageRepository(ImagesDatabase(application.applicationContext))

    val imageLiveData = MutableLiveData<Resource<ImageList>>()
    var imagePage = 1
    var imageResponse: ImageList? = null

    val searchImage = MutableLiveData<Resource<ImageSearch>>()
    var searchImagePage = 1
    var searchImageResponse: ImageSearch? = null

    val videoLiveData = MutableLiveData<Resource<ItemVideo>>()
    var videoPage = 3
    var videoResponse: ItemVideo? = null

    val searchVideo = MutableLiveData<Resource<ItemVideo>>()
    var searchVideoPage = 1
    var searchVideoResponse: ItemVideo? = null

    val imageSaveLiveData = MutableLiveData<ImageSave>()

    init {
        getResponseImages()
        getResponseVideo()
        Timber.d("$TAG init")
    }

    fun getResponseImages() = viewModelScope.launch {
        safeImageCall()
        Timber.d("$TAG getResponseImages")
    }


    fun getSearchImage(searchQuery: String) = viewModelScope.launch {
        safeSearchImageCall(searchQuery)
    }

        fun saveImage(image: ImageSave) = viewModelScope.launch {
        imageRepository.saveImage(image)
    }

    fun getImageSave() =
        imageRepository.getImageSave()

    fun getResponseVideo() = viewModelScope.launch {
        safeVideoCall()
        Timber.d("$TAG getResponseImages")
    }

    fun getSearchVideo(searchQuery: String) = viewModelScope.launch {
        safeSearchVideoCall(searchQuery)
    }


    fun insert(image: List<ImageListItem>) = viewModelScope.launch {
        imageRepository.insert(image)
    }

    fun getAllImage() = imageRepository.getAllImage()

    fun deleteAll() = Completable.fromAction {
        imageRepository.deleteAll()
    }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    private fun getImage(response: Response<ImageList>):
            Resource<ImageList> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                imagePage++
                Timber.d("$TAG getImage->imagePage->$imagePage")
                if (imageResponse == null) {
                    imageResponse = resultResponse
                    Timber.d("$TAG getImage->imageResponse->$imageResponse")
                } else {
                    val oldImage = imageResponse
                    oldImage!!.addAll(resultResponse)
                    Timber.d("$TAG getImage->oldImage->$oldImage")

                }
                Timber.d("$TAG getImage-> Resource.Success->$resultResponse")
                return Resource.Success(imageResponse ?: resultResponse)
            }
        }
        Timber.e("$TAG getImage->Resource.Error->${response.message()}")
        return Resource.Error(response.message())
    }


    private suspend fun safeImageCall() {
        imageLiveData.postValue(Resource.Loading())
        try {
            if (true) {
                val response = imageRepository
                    .getPhoto(imagePage)
                val d = getImage(response)
                imageLiveData.postValue(d)
                Timber.d("$TAG safeImageCall-> OK")
            } else {
                imageLiveData.postValue(Resource.Error("No internet Connection"))
                Timber.d("$TAG safeImageCall-> No internet Connection")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    imageLiveData.postValue(Resource.Error("Network Failure"))
                    Timber.e("$TAG safeImageCall-> Network Failure")
                }
                else -> {
                    imageLiveData.postValue(Resource.Error("Conversion Error"))
                    Timber.e("$TAG safeImageCall-> Conversion Error")
                }

            }
        }
    }

    private suspend fun safeSearchImageCall(searchQuery: String) {
        searchImage.postValue(Resource.Loading())
        try {
            val resource = imageRepository
                .searchForImage(searchImagePage, searchQuery)
            searchImage.postValue(handleSearchImageResponse(resource))

        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchImage.postValue(Resource.Error("Network Failure"))
                else -> searchImage.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleSearchImageResponse(response: Response<ImageSearch>):
            Resource<ImageSearch> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchImagePage++
                Timber.d("$TAG handleSearchImageResponse->resultResponse->$searchImagePage")
                if (searchImageResponse == null) {
                    searchImageResponse = resultResponse
                } else {
                    val oldArticle = searchImageResponse!!
                    oldArticle.results.addAll(resultResponse.results)
                }
                return Resource.Success(searchImageResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeVideoCall() {
        videoLiveData.postValue(Resource.Loading())
        try {
            if (true) {
                val response = imageRepository
                    .getVideo(videoPage)

                videoLiveData.postValue(getVideo(response))
                Timber.d("$TAG safeImageCall-> OK")
            } else {
                videoLiveData.postValue(Resource.Error("No internet Connection"))
                Timber.d("$TAG safeImageCall-> No internet Connection")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    videoLiveData.postValue(Resource.Error("Network Failure"))
                    Timber.e("$TAG safeImageCall-> Network Failure")
                }
                else -> {
                    videoLiveData.postValue(Resource.Error("Conversion Error"))
                    Timber.e("$TAG safeImageCall-> Conversion Error")
                }

            }
        }
    }

    private fun getVideo(response: Response<ItemVideo>): Resource<ItemVideo> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                videoPage++
                Timber.d("$TAG getImage->imagePage->$videoPage")
                if (videoResponse == null) {
                    videoResponse = resultResponse
                    Timber.d("$TAG getImage->imageResponse->$videoResponse")
                } else {
                    val oldImage = videoResponse
                    oldImage!!.videos.addAll(resultResponse.videos)
                    Timber.d("$TAG getImage->oldImage->$oldImage")

                }
                Timber.d("$TAG getImage-> Resource.Success->$resultResponse")
                return Resource.Success(videoResponse ?: resultResponse)
            }
        }
        Timber.e("$TAG getImage->Resource.Error->${response.message()}")
        return Resource.Error(response.message())
    }


    private suspend fun safeSearchVideoCall(searchQuery: String) {

        searchVideo.postValue(Resource.Loading())
        try {
            val response = imageRepository
                .searchForVideo(searchQuery, searchVideoPage)

            searchVideo.postValue(handleSearchVideoResponse(response))
            Timber.d("$TAG safeSearchVideoCall-> OK")
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    searchVideo.postValue(Resource.Error("Network Failure"))
                    Timber.e("$TAG safeSearchVideoCall-> Network Failure")
                }
                else -> {
                    searchVideo.postValue(Resource.Error("Conversion Error"))
                    Timber.e("$TAG safeSearchVideoCall-> Conversion Error")
                }

            }
        }
    }


    private fun handleSearchVideoResponse(response: Response<ItemVideo>):
            Resource<ItemVideo> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchVideoPage++
                Timber.d("$TAG handleSearchVideoResponse->resultResponse->$searchVideoPage")
                if (searchImageResponse == null) {
                    searchVideoResponse = resultResponse
                } else {
                    val oldArticle = searchVideoResponse!!
                    oldArticle.videos.addAll(resultResponse.videos)
                }
                return Resource.Success(searchVideoResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


}