package com.dafay.demo.biz.settings

import android.util.Log
import java.math.BigDecimal
import java.math.RoundingMode


/**
 * @Des
 * @Author m1studio
 * @Date 2024/2/1
 * <a href=" ">相关链接</a>
 */
object NumberUtils {
    private val TAG = NumberUtils::class.java.simpleName
    private const val THOUSAND = 1000.0
    private const val MILLIONS = 1000000.0
    private const val BILLION = 1000000000.0
    private const val THOUSAND_UNIT = "K"
    private const val MILLION_UNIT = "M"
    private const val BILLION_UNIT = "B"

    /**
     * 将数字转换成以万为单位或者以亿为单位，因为在前端数字太大显示有问题
     */
    fun amountConversion(amount: Double): String {
        //最终返回的结果值
        var result = amount.toString()
        //四舍五入后的值
        var value = 0.0
        //转换后的值
        var tempValue = 0.0
        //余数
        var remainder = 0.0
        //大于1000小于1百万
        if (amount > THOUSAND && amount <= MILLIONS) {
            tempValue = amount / THOUSAND
            remainder = amount % THOUSAND
            //余数小于500则不进行四舍五入
            value = if (remainder < THOUSAND / 2) {
                formatNumber(tempValue, 1, false)
            } else {
                formatNumber(tempValue, 1, true)
            }
            //如果值刚好是1000000，则要变成1m
            result = if (value == THOUSAND) {
                zeroFill(value / THOUSAND) + MILLION_UNIT
            } else {
                zeroFill(value) + THOUSAND_UNIT
            }
        } else
            if (amount > MILLIONS && amount <= BILLION) { //大于1百万小于10亿
                tempValue = amount / MILLIONS
                remainder = amount % MILLIONS
                Log.d(TAG, "tempValue=$tempValue, remainder=$remainder")

                //余数小于500000则不进行四舍五入
                value = if (remainder < MILLIONS / 2) {
                    formatNumber(tempValue, 1, false)
                } else {
                    formatNumber(tempValue, 1, true)
                }
                //如果值刚好是10000万，则要变成1亿
                result = if (value == MILLIONS) {
                    zeroFill(value / MILLIONS) + BILLION_UNIT
                } else {
                    zeroFill(value) + MILLION_UNIT
                }
            } else if (amount > BILLION) {
                tempValue = amount / BILLION
                remainder = amount % BILLION
                Log.d(TAG, "tempValue=$tempValue, remainder=$remainder")

                //余数小于50000000则不进行四舍五入
                value = if (remainder < BILLION / 2) {
                    formatNumber(tempValue, 1, false)
                } else {
                    formatNumber(tempValue, 1, true)
                }
                result = zeroFill(value) + BILLION_UNIT
            } else {
                result = zeroFill(amount)
            }
        Log.d(TAG, "result=$result")
        return result
    }

    /**
     * 对数字进行四舍五入，保留2位小数
     *
     * @param number   要四舍五入的数字
     * @param decimal  保留的小数点数
     * @param rounding 是否四舍五入
     * @return
     */
    fun formatNumber(number: Double, decimal: Int, rounding: Boolean): Double {
        val bigDecimal = BigDecimal(number)
        return if (rounding) {
            bigDecimal.setScale(decimal, RoundingMode.HALF_UP).toDouble()
        } else {
            bigDecimal.setScale(decimal, RoundingMode.DOWN).toDouble()
        }
    }

    /**
     * 对四舍五入的数据进行补0显示，即显示.0
     *
     * @return
     */
    fun zeroFill(number: Double): String {
        var value = number.toString()
        if (value.indexOf(".") < 0) {
            value = "$value"
        } else {
            val decimalValue = value.substring(value.indexOf(".") + 1)
            if ("0".equals(decimalValue)) {
                value = value.substring(0,value.indexOf("."))
            } else if (decimalValue.length < 2) {
                value = value
            }
        }
        return value
    }
}

