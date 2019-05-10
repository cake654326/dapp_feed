package com.dong.dapp.network

import com.dong.dapp.bean.areacode.ResultAreaCodeBean
import com.dong.dapp.bean.cash.ResultCashBalanceBean
import com.dong.dapp.bean.cash.ResultCashDailyIncomeBean
import com.dong.dapp.bean.cash.ResultCashRecordDetailBean
import com.dong.dapp.bean.cash.ResultCashRecordsBean
import com.dong.dapp.bean.coin.RequestCoinBalanceBean
import com.dong.dapp.bean.coin.RequestCoinRecordsBean
import com.dong.dapp.bean.coin.ResultCoinBalanceBean
import com.dong.dapp.bean.coin.ResultCoinRecordsBean
import com.dong.dapp.bean.common.ResultCommonConfigurationBean
import com.dong.dapp.bean.update.ResultUpdateInfoBean
import com.dong.dapp.bean.gamesquare.ResultAnnouncementBean
import com.dong.dapp.bean.gamesquare.ResultDAppBean
import com.dong.dapp.bean.kyc.*
import com.dong.dapp.bean.login.RequestLoginBean
import com.dong.dapp.bean.login.RequestVerifyCodeBean
import com.dong.dapp.bean.login.ResultLoginBean
import com.dong.dapp.bean.login.ResultVerifyCodeBean
import com.dong.dapp.bean.me.ResultUserInfoBean
import com.dong.dapp.bean.multipage.RequestMultiPageBean
import com.dong.dapp.bean.recharge.RequestRechargeOrderBean
import com.dong.dapp.bean.recharge.ResultRechargeOptionsBean
import com.dong.dapp.bean.recharge.ResultRechargeOrderBean
import com.dong.dapp.bean.statistics.RequestEnterDAppBean
import com.dong.dapp.bean.statistics.RequestExitDAppBean
import com.dong.dapp.bean.statistics.ResultEnterDAppBean
import com.dong.dapp.bean.upload.ResultOSSUploadPermissionBean
import com.dong.dapp.bean.wallet.TronSignBean
import com.dong.dapp.bean.wallet.UserInfoBean
import com.dong.dapp.extensions.decrypt
import com.dong.dapp.network.api.*
import com.dong.dapp.utils.AssetsUtils
import com.dong.dapp.utils.LocaleUtils
import io.reactivex.Observable
import me.serenadehl.base.extensions.async
import me.serenadehl.base.extensions.fromJsonArray
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

/**
 * 作者：Serenade
 * 邮箱：SerenadeHL@163.com
 * 创建时间：2019-04-10 19:46:44
 */
object RequestManager {

    //=============================================通用接口=============================================

    /**
     * 获取通用配置
     */
    fun getCommonConfiguration(): Observable<ResultCommonConfigurationBean?> {
        return RetrofitHelper.create(CommonApi::class.java)
            .getCommonConfiguration()
            .decrypt<ResultCommonConfigurationBean?>()
            .async()
    }

    /**
     * 获取版本更新信息
     */
    fun getUpdateInfo(): Observable<ResultUpdateInfoBean?> {
        return RetrofitHelper.create(CommonApi::class.java)
            .getUpdateInfo()
            .decrypt<ResultUpdateInfoBean?>()
            .async()
    }

    /**
     * 获取公告
     */
    fun getAnnouncement(): Observable<ResultAnnouncementBean?> {
        return RetrofitHelper.create(CommonApi::class.java)
            .getAnnouncement()
            .decrypt<ResultAnnouncementBean?>()
            .async()
    }

    /**
     * 获取DApp列表
     * @param page 页数
     * @param pageSize 每页条数
     */
    fun getDAppList(page: Int, pageSize: Int): Observable<ResultDAppBean?> {
        val requestBean = RequestMultiPageBean(page, pageSize)
        return RetrofitHelper.create(CommonApi::class.java)
            .getDAppList(requestBean)
            .decrypt<ResultDAppBean?>()
            .async()
    }


    //=============================================上传文件接口=============================================

    /**
     * 获取oss客户端上传权限（限制版）
     */
    fun getOSSUploadPermission(): Observable<ResultOSSUploadPermissionBean?> {
        return RetrofitHelper.create(UploadApi::class.java)
            .getOSSUploadPermission()
            .decrypt<ResultOSSUploadPermissionBean?>()
            .async()
    }

    //=============================================统计接口=============================================
    /**
     * 进入DApp
     */
    fun enterDApp(pid: String): Observable<ResultEnterDAppBean?> {
        val requestBean = RequestEnterDAppBean(pid)
        return RetrofitHelper.create(StatisticsApi::class.java)
            .enterDApp(requestBean)
            .decrypt<ResultEnterDAppBean?>()
            .async()
    }

    /**
     * 退出DApp
     */
    fun exitDApp(id: String, action: List<Map<String, String>>): Observable<BaseResponse> {
        val requestBean = RequestExitDAppBean(id, action)
        return RetrofitHelper.create(StatisticsApi::class.java)
            .exitDApp(requestBean)
    }


    //=============================================用户信息接口=============================================

    /**
     * 获取用户信息
     */
    fun getUserInfo(): Observable<ResultUserInfoBean?> {
        return RetrofitHelper.create(UserApi::class.java)
            .getUserInfo()
            .decrypt<ResultUserInfoBean?>()
            .async()
    }

    //=============================================登录接口=============================================

    /**
     * 获取验证码
     * @param phone 手机号
     * @param areaCode 区号
     */
    fun getVerifyCode(phone: String, areaCode: String): Observable<ResultVerifyCodeBean?> {
        val requestBean = RequestVerifyCodeBean(0, phone, areaCode)
        return RetrofitHelper.create(LoginApi::class.java)
            .getVerifyCode(requestBean)
            .decrypt<ResultVerifyCodeBean?>()
            .async()
    }

    /**
     * 登录
     * @param verifyCode 验证码
     * @param fp getVerifyCode接口返回数据
     * @param invitationCode 邀请码
     */
    fun login(verifyCode: String, fp: String, invitationCode: String): Observable<ResultLoginBean?> {
        val requestBean = RequestLoginBean(verifyCode, fp, invitationCode)
        return RetrofitHelper.create(LoginApi::class.java)
            .login(requestBean)
            .decrypt<ResultLoginBean?>()
            .async()
    }

    /**
     * 获取各个国家区号
     */
    fun getAreaCode(): Observable<List<ResultAreaCodeBean>?> {
        val observable: Observable<List<ResultAreaCodeBean>?> = Observable.create {
            val fileName = if (LocaleUtils.isCN()) {
                "country_cn.json"
            } else {
                "country_en.json"
            }
            val json = AssetsUtils.getAssets(fileName)
            try {
                val data: List<ResultAreaCodeBean> = json.fromJsonArray(ResultAreaCodeBean::class.java)!!
                it.onNext(data)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
        return observable.async()
    }


    //=============================================金币相关接口=============================================

    /**
     * 获取金币资产
     * @param protocol 公链类型 tron 0 EOS 1 ETH 2
     */
    fun getCoinBalance(protocol: Int): Observable<ResultCoinBalanceBean?> {
        val requestBean = RequestCoinBalanceBean(protocol)
        return RetrofitHelper.create(CoinApi::class.java)
            .getCoinBalance(requestBean)
            .decrypt<ResultCoinBalanceBean?>()
            .async()
    }

    /**
     * 获取金币流水
     * @param protocol 公链类型 tron 0 EOS 1 ETH 2
     * @param page 页数
     * @param pageSize 条数
     */
    fun getCoinRecords(protocol: Int, page: Int, pageSize: Int): Observable<ResultCoinRecordsBean?> {
        val requestBean = RequestCoinRecordsBean(protocol, page, pageSize)
        return RetrofitHelper.create(CoinApi::class.java)
            .getCoinRecords(requestBean)
            .decrypt<ResultCoinRecordsBean?>()
            .async()
    }


    //=============================================现金相关接口=============================================

    /**
     * 获取现金每日收益
     */
    fun getCashDailyIncome(): Observable<ResultCashDailyIncomeBean?> {
        return RetrofitHelper.create(CashApi::class.java)
            .getCashDailyIncome()
            .decrypt<ResultCashDailyIncomeBean?>()
            .async()
    }

    /**
     * 获取现金资产
     */
    fun getCashBalance(): Observable<ResultCashBalanceBean?> {
        return RetrofitHelper.create(CashApi::class.java)
            .getCashBalance()
            .decrypt<ResultCashBalanceBean?>()
            .async()
    }

    /**
     * 获取现金流水
     * @param page 页数
     * @param pageSize 条数
     */
    fun getCashRecords(page: Int, pageSize: Int): Observable<ResultCashRecordsBean?> {
        return RetrofitHelper.create(CashApi::class.java)
            .getCashRecords(page, pageSize)
            .decrypt<ResultCashRecordsBean?>()
            .async()
    }

    /**
     * 获取现金流水详情
     * @param recordId 流水id
     */
    fun getCashRecordDetail(recordId: String): Observable<ResultCashRecordDetailBean?> {
        return RetrofitHelper.create(CashApi::class.java)
            .getCashRecordDetail(recordId)
            .decrypt<ResultCashRecordDetailBean?>()
            .async()
    }


    //=============================================充值相关接口=============================================

    /**
     * 获取充值商品列表
     */
    fun getRechargeOptions(): Observable<ResultRechargeOptionsBean?> {
        return RetrofitHelper.create(RechargeApi::class.java)
            .getRechargeOptions()
            .decrypt<ResultRechargeOptionsBean?>()
            .async()
    }

    /**
     * 获取充值订单信息
     * @param product_id 商品id
     */
    fun getRechargeOrder(id: String): Observable<ResultRechargeOrderBean?> {
        val requestBean = RequestRechargeOrderBean(id)
        return RetrofitHelper.create(RechargeApi::class.java)
            .getRechargeOrder(requestBean)
            .decrypt<ResultRechargeOrderBean?>()
            .async()
    }


    //=============================================Tron相关接口=============================================

    /**
     * 获取Tron用户信息
     */
    fun getTronUserInfo(): Observable<UserInfoBean?> {
        return RetrofitHelper.create(TronApi::class.java)
            .getTronUserInfo("product")
            .decrypt<UserInfoBean?>()
            .async()
    }

    /**
     * 获取Tron签名
     * @param publicKey
     * @param message
     * @param type
     */
    fun getTronSign(publicKey: String, message: String, type: String): Observable<TronSignBean?> {
        return RetrofitHelper.create(TronApi::class.java)
            .getTronSign(publicKey, message, type, "product")
            .decrypt<TronSignBean?>()
            .async()
    }


    //=============================================KYC相关接口=============================================

    /**
     * 获取智能KYC的BizToken
     */
    //TODO 无解密
    fun getKYCBizToken(
        sign: String,
        idCardName: String,
        idCardNumber: String
    ): Observable<ResultKYCBizTokenBean?> {
        return RetrofitHelper.create(KYCApi::class.java)
            .getKYCBizToken(sign, idCardName, idCardNumber, "hmac_sha1", "meglive", 1)
            .async()
    }

    /**
     * 获取智能KYC的签名
     */
    fun getKYCSign(): Observable<ResultKYCSignBean?> {
        return RetrofitHelper.create(KYCApi::class.java)
            .getKYCSign()
            .decrypt<ResultKYCSignBean?>()
            .async()
    }

    /**
     * 判断身份证号是否可用
     * @param idNumber 身份证号
     */
    fun isIdCardNumberAvailable(idNumber: String): Observable<ResultIdCardNumberAvailableBean?> {
        val info = RequestIdCardNumberAvailableBean(idNumber)
        return RetrofitHelper.create(KYCApi::class.java)
            .isIdCardNumberAvailable(info)
            .decrypt<ResultIdCardNumberAvailableBean?>()
            .async()
    }

    /**
     * KYC验证完成通知后端
     */
    fun finishKYC(info: RequestKYCInfoBean): Observable<ResultFinishKYCBean?> {
        return RetrofitHelper.create(KYCApi::class.java)
            .finishKYC(info)
            .decrypt<ResultFinishKYCBean?>()
            .async()
    }
}