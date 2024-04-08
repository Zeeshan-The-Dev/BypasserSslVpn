package com.v2ray.ang.remote.callback

/**
 * Created by zeeshan irfan on 03/10/2019.
 */


interface IGenericCallBack {
    fun success(apiName: String, response: Any?)

    fun failure(apiName: String,message: String?)

}