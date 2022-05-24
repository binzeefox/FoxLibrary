package com.binzee.foxlib.lib_kotlin.utils.resource

import android.util.Patterns
import java.lang.IllegalArgumentException
import java.security.MessageDigest
import java.util.*
import java.util.regex.Pattern
import kotlin.experimental.and

/**
 * StringTools
 *
 * 字符串工具
 * @since  2022/5/24 11:39
 * @author tong.xiwen
 */
object StringTools {

    /**
     * 是否包含中文
     */
    val String.hasChinese: Boolean
        get() {
            if (isEmpty()) return false
            for (c in toCharArray())
                if (c.code in 0x4e00..0x9fa5) return true
            return false
        }

    /**
     * 是否是合法网络Url
     */
    val String.isWebUrl: Boolean
        get() = Patterns.WEB_URL.matcher(this).matches()

    /**
     * 是否是中华人民共和国居民身份号
     */
    val String.isCNIDCardCode: Boolean
        get() {
            val pattern = Pattern.compile("\\d{15}(\\d{2}[0-9xX])?")
            return pattern.matcher(this).matches()
        }

    /**
     * 生成md5摘要
     */
    val String.md5: String
        get() {
            if (isEmpty()) return ""
            val md5 = MessageDigest.getInstance("MD5")
            val bytes = md5.digest(toByteArray())
            val sb = StringBuilder()
            for (b in bytes) {
                val temp = Integer.toHexString((b and 0xff.toByte()).toInt())
                if (temp.length == 1) sb.append(0)
                sb.append(temp)
            }
            return sb.toString()
        }

    /**
     * 转为身份证相关数据类
     */
    val String.toIDCard: IIDCard
        get() {
            if (!isCNIDCardCode) throw IllegalArgumentException("string is not id card code!!!")
            return IDCard(this)
        }
}

/**
 * 身份证抽象
 *
 * @property province   省份
 * @property provinceCode   省份代码
 * @property birthDay   生日
 * @property isMale 是否是男性
 */
interface IIDCard {
    val province: String
    val provinceCode: String
    val birthDay: Date
    val isMale: Boolean
    val id: String
}

class IDCard(cardCode: String) : IIDCard {
    companion object {
        private fun getBirthday(cardCode: String): Date {
            val birthday = cardCode.substring(6, 14)
            val year = Integer.parseInt(birthday.substring(0, 4))
            val month = Integer.parseInt(birthday.substring(4, 6))
            val day = Integer.parseInt(birthday.substring(6))

            val calendar = Calendar.getInstance(Locale.CHINA).apply {
                set(year, month, day)
            }

            return calendar.time
        }

        private fun isMale(cardCode: String): Boolean {
            val sex = cardCode.substring(cardCode.length - 2, cardCode.length - 1)
            return sex.toLong() % 2 == 1L
        }
    }

    private val cProvinceMap = HashMap<String, String>().apply {
        put("11", "北京")
        put("12", "天津")
        put("13", "河北")
        put("14", "山西")
        put("15", "内蒙古")
        put("21", "辽宁")
        put("22", "吉林")
        put("23", "黑龙江")
        put("31", "上海")
        put("32", "江苏")
        put("33", "浙江")
        put("34", "安徽")
        put("35", "福建")
        put("36", "江西")
        put("37", "山东")
        put("41", "河南")
        put("42", "湖北")
        put("43", "湖南")
        put("44", "广东")
        put("45", "广西")
        put("46", "海南")
        put("50", "重庆")
        put("51", "四川")
        put("52", "贵州")
        put("53", "云南")
        put("54", "西藏")
        put("61", "陕西")
        put("62", "甘肃")
        put("63", "青海")
        put("64", "宁夏")
        put("65", "新疆")
        put("71", "台湾")
        put("81", "香港")
        put("82", "澳门")
        put("91", "境外")
    }   //身份证地理字典

    override val id: String = cardCode
    override val provinceCode: String = cardCode.substring(0, 2)
    override val province: String = cProvinceMap[provinceCode] ?: "未知"
    override val birthDay: Date = getBirthday(cardCode)
    override val isMale: Boolean = isMale(cardCode)
}