package com.dong.dapp.bean.common

import com.dong.dapp.bean.BaseBean
import com.google.gson.annotations.SerializedName

/**
 * 作者：Serenade
 * 邮箱：SerenadeHL@163.com
 * 创建时间：2019-05-06 11:37:21
 */
data class ResultCommonConfigurationFloatingBean(
    @SerializedName("img") val img: String,
    @SerializedName("path_ios") val pathIos: String,
    @SerializedName("path_android") val pathAndroid: String
):BaseBean()