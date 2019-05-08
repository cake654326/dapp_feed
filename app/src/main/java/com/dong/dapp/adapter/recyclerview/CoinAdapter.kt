package com.dong.dapp.adapter.recyclerview

import android.support.v4.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dong.dapp.R
import com.dong.dapp.constant.Router
import com.dong.dapp.constant.RouterParams
import com.dong.dapp.bean.coin.ResultCoinRecordsItemBean

/**
 * 作者：Serenade
 * 邮箱：SerenadeHL@163.com
 * 创建时间：2019-04-24 20:27:47
 */
class CoinAdapter : BaseQuickAdapter<ResultCoinRecordsItemBean, BaseViewHolder>(R.layout.app_recycle_item_coin) {
    init {
        setOnItemClickListener { _, _, position ->
            //TODO 传递完整数据 而非hash ID
            ARouter.getInstance()
                .build(Router.TRANSFER_DETAIL_ACTIVITY)
                .withString(RouterParams.ID, getItem(position)?.hash)
                .navigation()
        }
    }

    override fun convert(helper: BaseViewHolder?, item: ResultCoinRecordsItemBean?) {
        helper?.apply {
            setText(R.id.tv_title, item?.data)
            setText(R.id.tv_time, item?.timeStamp)
            setText(R.id.tv_money, item?.value)
            when (item?.confirmed) {
                0 -> {
                    setText(R.id.tv_status, R.string.ongoing)
                    setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.C1))
                }
                1 -> {
                    setText(R.id.tv_status, R.string.finished)
                    setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.C8))
                }
                else -> {
                    setText(R.id.tv_status, R.string.failed)
                    setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.C5))
                }
            }
        }
    }
}