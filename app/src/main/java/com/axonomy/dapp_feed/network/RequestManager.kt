package com.axonomy.dapp_feed.network

import android.annotation.SuppressLint
import android.content.Context
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.axonomy.dapp_feed.bean.areacode.ResultAreaCodeBean
import com.axonomy.dapp_feed.bean.cash.ResultCashBalanceBean
import com.axonomy.dapp_feed.bean.cash.ResultCashDailyIncomeBean
import com.axonomy.dapp_feed.bean.cash.ResultCashRecordDetailBean
import com.axonomy.dapp_feed.bean.cash.ResultCashRecordsBean
import com.axonomy.dapp_feed.bean.coin.*
import com.axonomy.dapp_feed.bean.common.ResultCommonConfigurationBean
import com.axonomy.dapp_feed.bean.update.ResultUpdateInfoBean
import com.axonomy.dapp_feed.bean.gamesquare.ResultAnnouncementBean
import com.axonomy.dapp_feed.bean.gamesquare.ResultDAppBean
import com.axonomy.dapp_feed.bean.kyc.*
import com.axonomy.dapp_feed.bean.login.RequestLoginBean
import com.axonomy.dapp_feed.bean.login.RequestVerifyCodeBean
import com.axonomy.dapp_feed.bean.login.ResultLoginBean
import com.axonomy.dapp_feed.bean.login.ResultVerifyCodeBean
import com.axonomy.dapp_feed.bean.me.ResultUserInfoBean
import com.axonomy.dapp_feed.bean.multipage.RequestMultiPageBean
import com.axonomy.dapp_feed.bean.recharge.RequestRechargeOrderBean
import com.axonomy.dapp_feed.bean.recharge.ResultRechargeOptionsBean
import com.axonomy.dapp_feed.bean.recharge.ResultRechargeOrderBean
import com.axonomy.dapp_feed.bean.statistics.RequestEnterDAppBean
import com.axonomy.dapp_feed.bean.statistics.RequestExitDAppBean
import com.axonomy.dapp_feed.bean.statistics.ResultEnterDAppBean
import com.axonomy.dapp_feed.bean.dapp.RequestPublicKeyBean
import com.axonomy.dapp_feed.bean.dapp.RequestSignBean
import com.axonomy.dapp_feed.bean.upload.ResultOSSUploadPermissionBean
import com.axonomy.dapp_feed.bean.dapp.ResultSignBean
import com.axonomy.dapp_feed.bean.dapp.ResultPublicKeyBean
import com.axonomy.dapp_feed.bean.invitation.ResultInvitationHistoryBean
import com.axonomy.dapp_feed.extensions.decrypt
import com.axonomy.dapp_feed.network.api.*
import com.axonomy.dapp_feed.utils.AssetsUtils
import com.axonomy.dapp_feed.utils.LocaleUtils
import io.reactivex.Observable
import me.serenadehl.base.extensions.async
import me.serenadehl.base.extensions.fromJsonArray
import retrofit2.http.GET
import java.io.File

/**
 * ?????????Serenade
 * ?????????SerenadeHL@163.com
 * ???????????????2019-04-10 19:46:44
 */
object RequestManager {

    //=============================================????????????=============================================

    /**
     * ??????????????????
     */
    fun getCommonConfiguration(): Observable<ResultCommonConfigurationBean?> {
        return RetrofitHelper.create(CommonApi::class.java)
            .getCommonConfiguration()
            .decrypt<ResultCommonConfigurationBean?>()
            .async()
    }

    /**
     * ????????????????????????
     */
    fun getUpdateInfo(): Observable<ResultUpdateInfoBean?> {
        return RetrofitHelper.create(CommonApi::class.java)
            .getUpdateInfo()
            .decrypt<ResultUpdateInfoBean?>()
            .async()
    }

    /**
     * ????????????
     */
    fun getAnnouncement(): Observable<ResultAnnouncementBean?> {
        return RetrofitHelper.create(CommonApi::class.java)
            .getAnnouncement()
            .decrypt<ResultAnnouncementBean?>()
            .async()
    }

    /**
     * ??????DApp??????
     * @param page ??????
     * @param pageSize ????????????
     */
    fun getDAppList(page: Int, pageSize: Int): Observable<ResultDAppBean?> {
        val requestBean = RequestMultiPageBean(page, pageSize)
        return RetrofitHelper.create(CommonApi::class.java)
            .getDAppList(requestBean)
            .decrypt<ResultDAppBean?>()
            .async()
    }


    /**
     * @param page ??????
     * @param pageSize ????????????
     */
    fun getInvitationHistory(page: Int, pageSize: Int): Observable<ResultInvitationHistoryBean?> {
        val requestBean = RequestMultiPageBean(page, pageSize)
        return RetrofitHelper.create(CommonApi::class.java)
            .getInvitationHistory(requestBean)
            .decrypt<ResultInvitationHistoryBean?>()
            .async()
    }


    //=============================================??????????????????=============================================

    /**
     * ??????oss????????????????????????????????????
     */
    fun getOSSUploadPermission(): Observable<ResultOSSUploadPermissionBean?> {
        return RetrofitHelper.create(UploadApi::class.java)
            .getOSSUploadPermission()
            .decrypt<ResultOSSUploadPermissionBean?>()
    }

    /**
     * ?????????oss
     * @param context ???????????????
     * @param isPrivate ?????????????????????????????????
     * @param data ??????
     * @param ossUploadPermissionBean ??????????????????
     */
    @SuppressLint("CheckResult")
    fun uploadToOSS(
        context: Context,
        bucketName: String,
        endPoint: String,
        data: ByteArray,
        ossUploadPermissionBean: ResultOSSUploadPermissionBean
    ): Observable<String> {
        return Observable.create<String> {
            val fileDir = ossUploadPermissionBean.catalogue + File.separator + System.currentTimeMillis() + ".png"
            // ?????????????????????
            val put = PutObjectRequest(bucketName, fileDir, data)
            val credentialProvider = OSSStsTokenCredentialProvider(
                ossUploadPermissionBean.accessKeyId,
                ossUploadPermissionBean.accessKeySecret,
                ossUploadPermissionBean.securityToken
            )
            try {
                val client = OSSClient(context.applicationContext, endPoint, credentialProvider)
                client.putObject(put)
                it.onNext(endPoint + File.separator + fileDir)
                it.onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                it.onError(e)
            }
        }
    }

    //=============================================????????????=============================================
    /**
     * ??????DApp
     */
    fun enterDApp(pid: String): Observable<ResultEnterDAppBean?> {
        val requestBean = RequestEnterDAppBean(pid)
        return RetrofitHelper.create(StatisticsApi::class.java)
            .enterDApp(requestBean)
            .decrypt<ResultEnterDAppBean?>()
            .async()
    }

    /**
     * ??????DApp
     */
    fun exitDApp(id: String, action: List<Map<String, String>>): Observable<BaseResponse?> {
        val requestBean = RequestExitDAppBean(id, action)
        return RetrofitHelper.create(StatisticsApi::class.java)
            .exitDApp(requestBean)
            .map {
                it as BaseResponse?
            }
            .async()
    }


    //=============================================??????????????????=============================================

    /**
     * ??????????????????
     */
    fun getUserInfo(): Observable<ResultUserInfoBean?> {
        return RetrofitHelper.create(UserApi::class.java)
            .getUserInfo()
            .decrypt<ResultUserInfoBean?>()
            .async()
    }

    //=============================================????????????=============================================

    /**
     * ???????????????
     * @param phone ?????????
     * @param areaCode ??????
     */
    fun getVerifyCode(phone: String, areaCode: String): Observable<ResultVerifyCodeBean?> {
        val requestBean = RequestVerifyCodeBean(0, phone, areaCode)
        return RetrofitHelper.create(LoginApi::class.java)
            .getVerifyCode(requestBean)
            .decrypt<ResultVerifyCodeBean?>()
            .async()
    }

    /**
     * ??????
     * @param verifyCode ?????????
     * @param fp getVerifyCode??????????????????
     * @param invitationCode ?????????
     */
    fun login(verifyCode: String, fp: String, invitationCode: String): Observable<ResultLoginBean?> {
        val requestBean = RequestLoginBean(verifyCode, fp, invitationCode)
        return RetrofitHelper.create(LoginApi::class.java)
            .login(requestBean)
            .decrypt<ResultLoginBean?>()
            .async()
    }

    /**
     * ????????????????????????
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


    //=============================================??????????????????=============================================

    /**
     * ??????????????????
     * @param protocol ???????????? tron 0 EOS 1 ETH 2
     */
    fun getCoinBalance(protocol: Int): Observable<ResultCoinBalanceBean?> {
        val requestBean = RequestCoinBalanceBean(protocol)
        return RetrofitHelper.create(CoinApi::class.java)
            .getCoinBalance(requestBean)
            .decrypt<ResultCoinBalanceBean?>()
            .async()
    }

    /**
     * ??????????????????
     * @param protocol ???????????? tron 0 EOS 1 ETH 2
     * @param page ??????
     * @param pageSize ??????
     */
    fun getCoinRecords(protocol: Int, page: Int, pageSize: Int): Observable<ResultCoinRecordsBean?> {
        val requestBean = RequestCoinRecordsBean(protocol, page, pageSize)
        return RetrofitHelper.create(CoinApi::class.java)
            .getCoinRecords(requestBean)
            .decrypt<ResultCoinRecordsBean?>()
            .async()
    }

    /**
     * ????????????????????????
     */
    fun getRevenueRules(): Observable<ResultRevenueRulesBean?>{
        return RetrofitHelper.create(CoinApi::class.java)
            .getRevenueRules()
            .decrypt<ResultRevenueRulesBean?>()
            .async()
    }


    //=============================================??????????????????=============================================

    /**
     * ????????????????????????
     */
    fun getCashDailyIncome(): Observable<ResultCashDailyIncomeBean?> {
        return RetrofitHelper.create(CashApi::class.java)
            .getCashDailyIncome()
            .decrypt<ResultCashDailyIncomeBean?>()
            .async()
    }

    /**
     * ??????????????????
     */
    fun getCashBalance(): Observable<ResultCashBalanceBean?> {
        return RetrofitHelper.create(CashApi::class.java)
            .getCashBalance()
            .decrypt<ResultCashBalanceBean?>()
            .async()
    }

    /**
     * ??????????????????
     * @param page ??????
     * @param pageSize ??????
     */
    fun getCashRecords(page: Int, pageSize: Int): Observable<ResultCashRecordsBean?> {
        return RetrofitHelper.create(CashApi::class.java)
            .getCashRecords(page, pageSize)
            .decrypt<ResultCashRecordsBean?>()
            .async()
    }

    /**
     * ????????????????????????
     * @param recordId ??????id
     */
    fun getCashRecordDetail(recordId: String): Observable<ResultCashRecordDetailBean?> {
        return RetrofitHelper.create(CashApi::class.java)
            .getCashRecordDetail(recordId)
            .decrypt<ResultCashRecordDetailBean?>()
            .async()
    }


    //=============================================??????????????????=============================================

    /**
     * ????????????????????????
     */
    fun getRechargeOptions(): Observable<ResultRechargeOptionsBean?> {
        return RetrofitHelper.create(RechargeApi::class.java)
            .getRechargeOptions()
            .decrypt<ResultRechargeOptionsBean?>()
            .async()
    }

    /**
     * ????????????????????????
     * @param product_id ??????id
     */
    fun getRechargeOrder(id: String): Observable<ResultRechargeOrderBean?> {
        val requestBean = RequestRechargeOrderBean(id)
        return RetrofitHelper.create(RechargeApi::class.java)
            .getRechargeOrder(requestBean)
            .decrypt<ResultRechargeOrderBean?>()
            .async()
    }


    //=============================================DApp????????????=============================================

    /**
     * ????????????
     * @param protocol ???????????? tron 0 EOS 1 ETH 2
     */
    fun getPublicKey(protocol: Int): Observable<ResultPublicKeyBean?> {
        val requestBean = RequestPublicKeyBean(protocol)
        return RetrofitHelper.create(DAppApi::class.java)
            .getTronPublicKey(requestBean)
            .decrypt<ResultPublicKeyBean?>()
            .async()
    }

    /**
     * ????????????
     * @param protocol ???????????? tron 0 EOS 1 ETH 2
     */
    fun getSign(protocol: Int, message: String, type: String): Observable<ResultSignBean?> {
        val requestBean = RequestSignBean(protocol, message, type)
        return RetrofitHelper.create(DAppApi::class.java)
            .getTronSign(requestBean)
            .decrypt<ResultSignBean?>()
            .async()
    }


    //=============================================KYC????????????=============================================

    /**
     * ????????????KYC???BizToken
     */
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
     * ????????????KYC?????????
     */
    fun getKYCSign(): Observable<ResultKYCSignBean?> {
        return RetrofitHelper.create(KYCApi::class.java)
            .getKYCSign()
            .decrypt<ResultKYCSignBean?>()
            .async()
    }

    /**
     * ??????????????????????????????
     * @param idNumber ????????????
     */
    fun isIdCardNumberAvailable(idNumber: String): Observable<ResultIdCardNumberAvailableBean?> {
        val info = RequestIdCardNumberAvailableBean(idNumber)
        return RetrofitHelper.create(KYCApi::class.java)
            .isIdCardNumberAvailable(info)
            .decrypt<ResultIdCardNumberAvailableBean?>()
            .async()
    }

    /**
     * KYC????????????????????????
     */
    fun finishKYC(info: RequestKYCInfoBean): Observable<ResultFinishKYCBean?> {
        return RetrofitHelper.create(KYCApi::class.java)
            .finishKYC(info)
            .decrypt<ResultFinishKYCBean?>()
            .async()
    }
}