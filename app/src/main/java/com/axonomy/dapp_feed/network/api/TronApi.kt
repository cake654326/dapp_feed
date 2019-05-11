package com.axonomy.dapp_feed.network.api

import com.axonomy.dapp_feed.network.BaseResponse
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 作者：Serenade
 * 邮箱：SerenadeHL@163.com
 * 创建时间：2019-04-19 11:12:10
 */
interface TronApi {
    /**
     * 获取Tron用户信息
     * @param env 线上环境为product
     */
    //TODO 更改接口
    @POST("public/wallet/tron_userinfo")
    @FormUrlEncoded
    fun getTronUserInfo(@Field("env") env: String): Observable<BaseResponse>

    /**
     * 获取Tron签名
     * @param publicKey
     * @param message
     * @param type
     * @param env 线上环境为product
     */
    //TODO 更改接口
    @POST("public/wallet/tron_sign")
    @FormUrlEncoded
    fun getTronSign(
        @Field("public_key") publicKey: String,
        @Field("message") message: String,
        @Field("type") type: String,
        @Field("env") env: String
    ): Observable<BaseResponse>

}