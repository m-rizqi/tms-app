package com.rizqi.tms.model

import com.rizqi.tms.utility.getFormattedDateString
import java.util.*

sealed class DateRange(
    var ordinal : Int,
    var dateFrom : Long? = null,
    var dateTo : Long? = null,
) {
    class All() : DateRange(0)
    class Today() : DateRange(1){
        init {
            val cal = Calendar.getInstance()
            dateFrom = cal.timeInMillis
        }
    }
    class Yesterday() : DateRange(2){
        init {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -1)
            dateFrom = cal.timeInMillis
        }
    }
    class ThisWeek() : DateRange(3){
        init {
            val cal = Calendar.getInstance()
            dateTo = cal.timeInMillis
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            dateFrom = cal.timeInMillis
        }
    }
    class LastWeek() : DateRange(4){
        init {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            cal.add(Calendar.DATE, -1)
            dateTo = cal.timeInMillis
            cal.add(Calendar.DATE, -6)
            dateFrom = cal.timeInMillis
        }
    }
    class ThisMonth() : DateRange(5){
        init {
            val cal = Calendar.getInstance()
            dateTo = cal.timeInMillis
            cal.set(Calendar.DAY_OF_MONTH, 1)
            dateFrom = cal.timeInMillis
        }
    }
    class LastMonth() : DateRange(6){
        init {
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -1)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            dateFrom = cal.timeInMillis
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            dateTo = cal.timeInMillis
        }
    }
    class Custom() : DateRange(7)

    fun getDateRangeString() : String {
        return when(this){
            is LastMonth -> "${getFormattedDateString(this.dateFrom!!)} - ${getFormattedDateString(this.dateTo!!)}"
            is LastWeek -> "${getFormattedDateString(this.dateFrom!!)} - ${getFormattedDateString(this.dateTo!!)}"
            is ThisMonth -> "${getFormattedDateString(this.dateFrom!!)} - ${getFormattedDateString(this.dateTo!!)}"
            is ThisWeek -> "${getFormattedDateString(this.dateFrom!!)} - ${getFormattedDateString(this.dateTo!!)}"
            is Today -> getFormattedDateString(this.dateFrom!!) ?: ""
            is Yesterday -> getFormattedDateString(this.dateFrom!!) ?: ""
            else -> ""
        }
    }
}