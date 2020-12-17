package com.nurbk.ps.v1.image.ui.activty

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.nurbk.ps.v1.image.R
import com.nurbk.ps.v1.image.util.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_sheet.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var navController: NavController
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(toolbar)
            navController = findNavController(R.id.navHostFragment)
        setupActionBarWithNavController(navController, AppBarConfiguration(navController.graph))
        bottomBar.setupWithNavController(navController)

        bottomSheetBehavior = BottomSheetBehavior.from(menu_sheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        

        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.imagesFragment,
                    R.id.searchFragment,
                    R.id.favoriteFragment ,
                    R.id.videoFragment-> {
                        bottomBar.visibility = View.VISIBLE
                    }
                    else -> {
                        bottomBar.visibility = View.GONE
                    }
                }
            }
    }


    fun downloadImage(url: String) {
        Timber.d("$TAG downloadImage")
        Dexter.withContext(applicationContext)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    Timber.d("$TAG downloadImage->onPermissionGranted")
                    //permission granted from popup, perform download
                    startDownloading(url)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) { /* ... */
                    Timber.d("$TAG downloadImage->onPermissionDenied")
                    Toast.makeText(
                        applicationContext,
                        "You must Permission Granted to download",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    Timber.d("$TAG downloadImage->onPermissionRationaleShouldBeShown")

                }
            }).check()
    }

    private fun startDownloading(url: String) {
        //get url from edit text
        Timber.d("$TAG startDownloading->$url")
        //create download request
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(
            //allow type network to download files
            DownloadManager.Request.NETWORK_WIFI
                    or
                    DownloadManager.Request.NETWORK_MOBILE
        )
        request.setTitle("Downloaded")//set title in download notification
        request.setDescription("Downloading file...") //set description in download notification

        request.allowScanningByMediaScanner()
        request.setShowRunningNotification(true)
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(
            DownloadManager
                .Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )
        //get current timestamp as file name
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS, "${System.currentTimeMillis()}"
        )

        //get Download service and enque file
        val manager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        manager.enqueue(request)
        Timber.d("$TAG startDownloading->manager->$manager")



    }




}