package com.rizqi.tms.utility

import android.util.Log
import java.util.*

class ThousandFormatter {
    companion object {
        fun format(value: String) : String{
            val pointIdx = value.withIndex().find { it.value == ','}?.index
            var afterPointText : String? = null
            val noPointText = if (pointIdx == null) value else {
                afterPointText = value.substring(pointIdx+1, value.length)
                value.substring(0, pointIdx)
            }
            var formatted = formatNoFloatingPoint(noPointText.replace("\\.".toRegex(), ""))
            if (afterPointText != null){
                formatted += ",${afterPointText}"
            }
            return formatted
        }
        fun format(value : Double) = format(value.toString())
        private fun formatNoFloatingPoint(value : String) : String{
            val lst = StringTokenizer(value, ",")
            var str1 = value
            var str2 = ""
            if (lst.countTokens() > 1){
                str1 = lst.nextToken()
                str2 = lst.nextToken()
            }
            var str3 = ""
            var i = 0
            var j = -1 + str1.length
            if (str1[-1 + str1.length] == ','){
                j--
                str3 = ","
            }
            var k = j
            while (true){
                if (k < 0){
                    if (str2.isNotEmpty()){
                        str3 = "$str3,$str2"
                    }
                    return str3
                }
                if (i == 3){
                    str3 = ".$str3"
                    i = 0
                }
                str3 = str1[k] + str3
                i++
                k--
            }
        }
    }
}