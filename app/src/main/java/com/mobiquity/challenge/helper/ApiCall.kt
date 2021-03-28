package com.mobiquity.challenge.helper

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.mobiquity.challenge.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

object ApiCall {
    private const val CONNECT_TIMEOUT = 60
    private const val READ_TIMEOUT = 60
    private const val DELAY_API_CALLS = 2000 //2000 = 2 seconds
    private const val GENERIC_ERROR_MESSAGE =
        "Something went wrong. Please try again later"

    fun get(activity: Activity?, url: String?, apiResponse: ApiResponse?) {
        makeApiCall(ApiType.GET, activity, url, null, apiResponse)
    }

    fun post(
        activity: Activity?, url: String?, body: Map<String?, Any?>?, apiResponse: ApiResponse?
    ) {
        makeApiCall(ApiType.POST, activity, url, body, apiResponse)
    }

    private fun makeApiCall(
        apiType: ApiType, activity: Activity?, url: String?, body: Map<String?, Any?>?,
        apiResponse: ApiResponse?
    ) {
        Handler().postDelayed({

            if (activity == null || apiResponse == null) return@postDelayed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isDestroyed) return@postDelayed
            }
            if (!isNetworkAvailable(activity)) {
                Toast.makeText(activity, "No internet", Toast.LENGTH_SHORT).show()
                apiResponse.onApiFailure()
                return@postDelayed
            }

            val apiCall = getApiCall(apiType, activity, url)
            if (apiCall == null || url == null) {
                showGenericErrorAndReturnApiFailed(activity, apiResponse)
                return@postDelayed
            }

            if (BuildConfig.DEBUG) {
                Log.e(" url -- ", "" + url)
                if (body != null) Log.e(" body -- ", "" + JSONObject(body))
            }

            val call = getCall(apiCall, apiType, url, body)
            if (call == null) {
                showGenericErrorAndReturnApiFailed(activity, apiResponse)
                return@postDelayed
            }

            val activityWeakReference = WeakReference(activity)
            call.clone().enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    Log.e(" on response code", "" + response.code())
                    if (activityWeakReference.get() == null) {
                        return
                    }
                    val responseBody = response.body()
                    if (response.code() == 200) {
                        try {
                            val responseString: String
                            if (responseBody == null) {
                                showGenericErrorAndReturnApiFailed(
                                    activityWeakReference.get(),
                                    apiResponse
                                )
                            } else {
                                responseString = responseBody.string()
                                Log.e(
                                    " responseString ",
                                    "$url >>> \n$responseString"
                                )
                                apiResponse.onApiSuccess(responseString)
                            }
                        } catch (e: Exception) {
                            showGenericErrorAndReturnApiFailed(
                                activityWeakReference.get(),
                                apiResponse
                            )
                        }
                    } else {
                        showGenericErrorAndReturnApiFailed(activity, apiResponse)
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody?>,
                    t: Throwable
                ) {
                    showGenericErrorAndReturnApiFailed(activity, apiResponse)
                }
            })
        }, DELAY_API_CALLS.toLong())
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork
            val actNw = connectivityManager.getNetworkCapabilities(nw)
            if (nw == null || actNw == null) false else actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo
            nwInfo != null && nwInfo.isConnected
        }
    }

    private fun getCall(
        apiCall: ApiCalls,
        apiType: ApiType,
        url: String,
        body: Map<String?, Any?>?
    ): Call<ResponseBody>? {
        if (apiType == ApiType.GET) {
            return apiCall.apiGet(url)
        } else if (apiType == ApiType.POST) {
            return if (body == null) null else apiCall.apiPost(url, body)
        }
        return null
    }

    private fun showGenericErrorAndReturnApiFailed(
        activity: Activity?,
        apiResponse: ApiResponse?
    ) {
        if (activity == null || apiResponse == null) {
            return
        }
        Toast.makeText(activity, GENERIC_ERROR_MESSAGE, Toast.LENGTH_SHORT).show()
        apiResponse.onApiFailure()
    }

    private fun getApiCall(apiType: ApiType, activity: Activity, url: String?): ApiCalls? {
        return if (apiType == ApiType.GET || apiType == ApiType.POST) {
            getRetrofitObject(activity, url)
        } else null
    }

    private fun getRetrofitObject(
        activity: Activity?,
        url: String?
    ): ApiCalls? {
        if (activity == null) {
            return null
        }
        val okHttpClient = addHeaders(activity, url) ?: return null
        return Retrofit.Builder().baseUrl("https://google.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiCalls::class.java)
    }

    private fun addHeaders(activity: Activity?, url: String?): OkHttpClient? {
        if (activity == null || url == null) {
            return null
        }
        try {
            return OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .connectTimeout(
                    CONNECT_TIMEOUT.toLong(),
                    TimeUnit.SECONDS
                )
                .addInterceptor { chain: Interceptor.Chain ->
                    val original = chain.request()
                    val requestBuilder: Request.Builder
                    requestBuilder = original.newBuilder()
                        .method(original.method(), original.body())
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private enum class ApiType {
        GET, POST
    }

    interface ApiResponse {
        fun onApiSuccess(response: String?)
        fun onApiFailure()
    }

    internal interface ApiCalls {
        @GET
        fun apiGet(
            @Url url: String?
        ): Call<ResponseBody>?

        @POST
        fun apiPost(
            @Url url: String?,
            @Body body: Map<String?, Any?>?
        ): Call<ResponseBody>?
    }
}