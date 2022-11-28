package com.rizqi.tms.model

import com.rizqi.tms.utility.getFormattedDateString
import java.util.*

sealed class DateRange(
    var dateFrom : Long? = null,
    var dateTo : Long? = null
) {
    class All() : DateRange()
    class Today() : DateRange(){
        init {
            val cal = Calendar.getInstance()
            dateFrom = cal.timeInMillis
        }
    }
    class Yesterday() : DateRange(){
        init {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -1)
            dateFrom = cal.timeInMillis
        }
    }
    class ThisWeek() : DateRange(){
        init {
            val cal = Calendar.getInstance()
            dateTo = cal.timeInMillis
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            dateFrom = cal.timeInMillis
        }
    }
    class LastWeek() : DateRange(){
        init {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            cal.add(Calendar.DATE, -1)
            dateTo = cal.timeInMillis
            cal.add(Calendar.DATE, -6)
            dateFrom = cal.timeInMillis
        }
    }
    class ThisMonth() : DateRange(){
        init {
            val cal = Calendar.getInstance()
            dateTo = cal.timeInMillis
            cal.set(Calendar.DAY_OF_MONTH, 1)
            dateFrom = cal.timeInMillis
        }
    }
    class LastMonth() : DateRange(){
        init {
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -1)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            dateFrom = cal.timeInMillis
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            dateTo = cal.timeInMillis
        }
    }
    class Custom() : DateRange()

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