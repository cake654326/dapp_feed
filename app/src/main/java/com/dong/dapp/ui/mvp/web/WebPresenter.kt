package com.dong.dapp.ui.mvp.web

import com.dong.dapp.bean.statistics.ResultEnterDAppBean
import com.dong.dapp.network.BaseObserver
import me.serenadehl.base.base.mvpbase.MVPBasePresenter

/**
 *
 * 作者：Serenade
 * 邮箱：SerenadeHL@163.com
 * 创建时间：2019-4-11 10:37:01
 */
class WebPresenter : MVPBasePresenter<IWebView, IWebModel>(), IWebPresenter {

    override fun createModel() = WebModel()

    override fun enterDApp(pid: String) {
        mModel.enterDApp(pid)
            .subscribe(object :BaseObserver<ResultEnterDAppBean?>(){
                override fun next(data: ResultEnterDAppBean?) {
                    mView.get()?.enterDAppSuccess(data)
                }

                override fun error(error: Throwable) {
                    mView.get()?.enterDAppFailed()
                }
            })
    }
}
