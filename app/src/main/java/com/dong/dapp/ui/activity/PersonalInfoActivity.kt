package com.dong.dapp.ui.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.dong.dapp.R
import com.dong.dapp.bean.me.ResultUserInfoBean
import com.dong.dapp.ui.mvp.kyc.KYCActivity
import kotlinx.android.synthetic.main.activity_personal_info.*
import kotlinx.android.synthetic.main.title_layout.*
import me.serenadehl.base.base.BaseActivity
import me.serenadehl.base.extensions.log
import me.serenadehl.base.extensions.startActivity

/**
 * 作者：Serenade
 * 邮箱：SerenadeHL@163.com
 * 创建时间：2019-05-06 17:24:16
 */
class PersonalInfoActivity : BaseActivity() {
    private var mUserInfo: ResultUserInfoBean? = null

    private val mC2 by lazy { ContextCompat.getColor(this@PersonalInfoActivity, R.color.C2) }

    override fun layout() = R.layout.activity_personal_info

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //设置状态栏
        setupStatusBar()
        setStatusBarColor(mC2, true)
        //返回按钮
        iv_back.setOnClickListener { finish() }
        //设置标题栏
        tv_title.setText(R.string.personal_info)

        mUserInfo = intent.getParcelableExtra("data")

        tv_phone_number.text = mUserInfo?.account

        //TODO 根据kycStatus设置不同文案、背景以及按钮点击事件
//        when (mUserInfo?.kycStatus) {
//             -> {
//            }
//            else -> {
//            }
//        }
        btn_kyc.setOnClickListener { startActivity<KYCActivity>() }
    }

}