package com.dong.dapp.extensions

import android.util.Base64
import com.dong.dapp.exception.BaseException
import com.dong.dapp.network.BaseResponse
import com.dong.dapp.utils.AESUtils
import io.reactivex.Observable
import me.serenadehl.base.extensions.fromJson
import me.serenadehl.base.extensions.log

/**
 * 作者：Serenade
 * 邮箱：SerenadeHL@163.com
 * 创建时间：2019-04-23 13:22:37
 */

/**
 * 解密
 */
inline fun <reified T> Observable<BaseResponse>.decrypt(): Observable<T?> {
    return flatMap {
        if (it.code != 200) {
            return@flatMap Observable.error<BaseException>(BaseException(it.code, it.errMsg))
        } else {
            val decode = Base64.decode(it.data, Base64.DEFAULT)
            val decrypt = AESUtils.decrypt(decode, AESUtils.generateKey(), AESUtils.generateIv())
            "解密数据---------> $decrypt".log()
            val data: T? = decrypt?.fromJson(T::class.java)
            return@flatMap Observable.just(data)
        }
    }.map {
        it as T
    }
}
