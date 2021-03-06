package com.axonomy.dapp_feed.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import me.serenadehl.base.utils.img.GlideRoundTransform

/**
 * 作者：Serenade
 * 邮箱：SerenadeHL@163.com
 * 创建时间：2019-04-11 11:50:38
 */
inline fun ImageView.show(url: String?) {
    Glide.with(this).load(url ?: "").into(this)
}

inline fun ImageView.showRound(url: String?, radiusDp: Float) {
    Glide.with(this).load(url ?: "").transform(GlideRoundTransform(radiusDp)).into(this)
}