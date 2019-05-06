package com.dong.dapp.ui.mvp.totalcount.totalcashcount

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dong.dapp.R
import com.dong.dapp.adapter.recyclerview.CashAdapter
import com.dong.dapp.bean.cash.ResultCashBalanceBean
import com.dong.dapp.bean.cash.ResultCashRecordsBean
import com.dong.dapp.ui.mvp.totalcount.TotalCountParentActivity
import com.dong.dapp.ui.mvp.transfer.TransferCashActivity
import kotlinx.android.synthetic.main.activity_total_cash_count.*
import kotlinx.android.synthetic.main.app_recycle_header_total_cash.view.*
import me.serenadehl.base.extensions.dimen
import me.serenadehl.base.extensions.getStatusBarHeight
import me.serenadehl.base.extensions.log
import me.serenadehl.base.extensions.startActivity

/**
 * 现金资产页
 * 作者：Serenade
 * 邮箱：SerenadeHL@163.com
 * 创建时间：2019-4-23 16:26:19
 */
class TotalCashCountActivity : TotalCountParentActivity<ITotalCashCountPresenter>(), ITotalCashCountView {

    private val mAdapter by lazy { CashAdapter() }

    override fun layout() = R.layout.activity_total_cash_count

    override fun createPresenter() = TotalCashCountPresenter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (mHeader.tv_balance.layoutParams as ConstraintLayout.LayoutParams).topMargin += getStatusBarHeight() + dimen(R.dimen.L2)

        btn_transfer.setOnClickListener { startActivity<TransferCashActivity>() }
    }

    override fun getTitleResId() = R.string.cash_assets

    override fun getHeaderResId() = R.layout.app_recycle_header_total_cash

    override fun getRecyclerView(): RecyclerView = rv_cash

    override fun getAdapter(): BaseQuickAdapter<*, BaseViewHolder> = mAdapter

    override fun getHeaderData() {
        mPresenter.getCashBalance()
    }

    override fun loadData(refresh: Boolean) {
        super.loadData(refresh)
        mPresenter.getCashRecords(mPage, mPageSize, refresh)
    }

    @SuppressLint("SetTextI18n")
    override fun getCashBalanceSuccess(data: ResultCashBalanceBean?) {
        val moneyWithSymbol = getString(R.string.money_with_symbol)
        mHeader.tv_balance.text = String.format(moneyWithSymbol, data?.balance)
        mHeader.tv_cumulative_gain.text = String.format(moneyWithSymbol, data?.totalRevenue)
        mHeader.tv_today_obtain.text = String.format(moneyWithSymbol, data?.todayRevenue)
    }

    override fun getCashBalanceFailed() {

    }

    override fun getCashRecordsSuccess(data: ResultCashRecordsBean?, refresh: Boolean) {
        if (refresh) {
            mAdapter.setNewData(data?.items)
        } else {
            mAdapter.addData(data?.items ?: listOf())
            if (data?.hasMore() == true) {
                mAdapter.loadMoreComplete()
            } else {
                mAdapter.loadMoreEnd(true)
            }
        }
    }

    override fun getCashRecordsFailed() {
        "getCashRecordsFailed------->".log()
    }
}
