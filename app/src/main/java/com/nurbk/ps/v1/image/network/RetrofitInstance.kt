package com.nurbk.ps.v1.image.network

import android.util.Log
import com.nurbk.ps.v1.image.MyApplication
import com.nurbk.ps.v1.image.util.Constants.BASE_URL_PHOTO
import com.nurbk.ps.v1.image.util.Constants.BASE_URL_VIDEO
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class RetrofitInstance {


    companion object {
        var apiPhoto: ImageApi? = null
        var apiVideo: VideoApi? = null
        private const val cacheSize = 5 * 1024 * 1024 // 5 MB
            .toLong()
        const val HEADER_CACHE_CONTROL = "Cache-Control"
        const val HEADER_PRAGMA = "Pragma"

        init {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor())
                .cache(cache())
                .addNetworkInterceptor(networkInterceptor())
                .addInterceptor(offlineInterceptor())
                .build()


            apiPhoto = getInstantRetrofit(BASE_URL_PHOTO, client).create(ImageApi::class.java)
            apiVideo = getInstantRetrofit(BASE_URL_VIDEO, client).create(VideoApi::class.java)
        }

        private fun getInstantRetrofit(url: String, client: OkHttpClient) = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

        private fun cache(): Cache {
            return Cache(
                File(MyApplication.getInstance()!!.cacheDir, "someIdentifier"),
                cacheSize
            )
        }

        private fun offlineInterceptor(): Interceptor {
            return object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    Log.d("TAG", "offline interceptor: called.")
                    var request = chain.request()
                    if (!MyApplication.hasNetwork()) {
                        val cacheControl = CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build()
                        request = request.newBuilder()
                            .removeHeader(HEADER_PRAGMA)
                            .removeHeader(HEADER_CACHE_CONTROL)
                            .cacheControl(cacheControl)
                            .build()
                    }

                    return chain.proceed(request)
                }
            }
        }

        private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
            val httpLoggingInterceptor =
                HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        Log.d("TAG", "log: http log: $message")
                    }
                })
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            return httpLoggingInterceptor
        }

        private fun networkInterceptor(): Interceptor {
            return object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    Log.d("TAG", "network interceptor: called.")
                    val response: Response = chain.proceed(chain.request())
                    val cacheControl = CacheControl.Builder()
                        .maxAge(5, TimeUnit.SECONDS)
                        .build()
                    return response.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                        .build()
                }
            }
        }
    }
}



