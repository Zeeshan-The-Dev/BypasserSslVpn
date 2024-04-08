package com.v2ray.ang.remote

import com.v2ray.ang.BuildConfig
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val baseUrl = "https://vpngame.com/"
    var retrofit: Retrofit? = null
    val dispatcher = Dispatcher()

    fun getClient(): Retrofit? {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG)
            logging.level = HttpLoggingInterceptor.Level.BODY
        else
            logging.level = HttpLoggingInterceptor.Level.NONE
        if (retrofit == null) {
            retrofit = Retrofit.Builder().client(OkHttpClient().newBuilder().readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).retryOnConnectionFailure(false).dispatcher(
                            dispatcher
                    ).addInterceptor(Interceptor { chain: Interceptor.Chain? ->
                        //  val original: Request = chain!!.request()
                        val newRequest = chain?.request()!!.newBuilder()
                      //     newRequest.addHeader("authorization", "${AnytimeSecureApp.db.getString(Constants.ACCESS_TOKEN, "")}")
                        return@Interceptor chain.proceed(newRequest.build())
                    }).addInterceptor(logging).build())
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }
        return retrofit
    }



}