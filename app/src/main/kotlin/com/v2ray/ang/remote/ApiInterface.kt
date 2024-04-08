package com.v2ray.ang.remote

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface  ApiInterface {

    @FormUrlEncoded
    @POST("bypasser2024")
    fun loadFromServer(@Field("server") server : String): Call<ArrayList<String>>
}