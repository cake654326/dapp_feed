package com.axonomy.dapp_feed.utils

import android.util.Base64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Key
import java.security.Security
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 作者：Serenade
 * 邮箱：SerenadeHL@163.com
 * 创建时间：2019-04-22 14:09:18
 */
object AESUtils {

    /**
     * 生成密钥
     */
    fun generateKey(): String {
//        return "0102030405060708"
        return "17fee9978ff9563b"
    }

    /**
     * 生成偏移量
     */
    fun generateIv(): String {
//        return "0102030405060708"
        return "7a9351a1945c1a75"
    }

    /**
     * 初始化
     */
    private fun init(keyBytes: ByteArray): Holder {
        var keyBytesCopy = keyBytes
        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
        val base = 16
        if (keyBytesCopy.size % base != 0) {
//            val groups = keyBytesCopy.size / base + if (keyBytesCopy.size % base != 0) 1 else 0
            val groups = keyBytesCopy.size / base + 1
            val temp = ByteArray(groups * base)
            Arrays.fill(temp, 0.toByte())
            System.arraycopy(keyBytesCopy, 0, temp, 0, keyBytesCopy.size)
            keyBytesCopy = temp
        }
        // 初始化
        Security.addProvider(BouncyCastleProvider())
        // 转化成JAVA的密钥格式
        // 算法名称
        val holder = Holder()
        holder.key = SecretKeySpec(keyBytesCopy, "AES")
        try {
            // 初始化cipher
            // 加解密算法/模式/填充方式
            val algorithmStr = "AES/CBC/PKCS7Padding"
            holder.cipher = Cipher.getInstance(algorithmStr, "BC")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return holder
    }

    /**
     * 加密方法
     *
     * @param content   要加密的字符串
     * @param key       密钥
     * @param iv        偏移量
     * @return          加密过后的字符串
     */
    fun encrypt(content: String, key: String, iv: String): String? {
        val holder = init(key.toByteArray())
        try {
            holder.cipher.init(Cipher.ENCRYPT_MODE, holder.key, IvParameterSpec(iv.toByteArray()))
            val data = holder.cipher.doFinal(content.toByteArray())
            return Base64.encodeToString(data, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 解密方法
     *
     * @param content   要解密的字符串
     * @param key       密钥
     * @param iv        偏移量
     * @return
     */
    fun decrypt(content: ByteArray, key: String, iv: String): String? {
        val holder = init(key.toByteArray())
        try {
            holder.cipher.init(Cipher.DECRYPT_MODE, holder.key, IvParameterSpec(iv.toByteArray()))
            return String(holder.cipher.doFinal(content))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    internal class Holder {
        lateinit var key: Key
        lateinit var cipher: Cipher
    }
}