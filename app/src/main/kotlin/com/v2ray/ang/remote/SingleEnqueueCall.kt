package com.v2ray.ang.remote

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.v2ray.ang.remote.callback.IGenericCallBack
import java.io.IOException
import java.net.UnknownHostException


object SingleEnqueueCall {



    fun <T> callRetrofit(
            call: Call<T>,
            strApiName : String,
            isLoaderShown: Boolean,
            apiListener: IGenericCallBack
    ) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    apiListener.success(strApiName, response.body())
                } else {
                    when {
                        response.code() == 401 -> {
                            //onTokenExpiredLogout()
                            return
                        }
                        response.errorBody() != null -> try {
                            val gson = GsonBuilder().create()
                            try {
                                apiListener.failure(strApiName, "Server Error")
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                apiListener.failure(strApiName, "Server Not Responding")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            apiListener.failure(strApiName, "Server Not Responding")
                        }
                        else -> {
                            apiListener.failure(strApiName, "Server Not Responding")
                            return
                        }
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val callBack = this
                if (t.message != "Canceled") {
                    if (t is UnknownHostException || t is IOException) {

                        apiListener.failure(strApiName, "No Internet")
                    } else {
                        apiListener.failure(strApiName, t.toString())
                    }
                }
            }
        })
    }

    fun <T> enqueueWithRetry(call: Call<T>, callback: Callback<T>, isLoaderShown: Boolean) {
        call.clone().enqueue(callback)
    }
}
