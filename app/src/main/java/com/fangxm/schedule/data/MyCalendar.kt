package com.fangxm.schedule.data

import java.util.*

class MyCalendar {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    private val mdays = arrayOf(31,28,31,30,31,30,31,31,30,31,30,31)

    constructor() {

    }

    constructor(calendar: Calendar) {
        this.year = calendar.get(Calendar.YEAR)
        this.month = calendar.get(Calendar.MONTH)
        this.day = calendar.get(Calendar.DAY_OF_MONTH)
    }

    constructor(year: Int, month: Int, day: Int) {
        this.year = year
        this.month = month
        this.day = day
    }

    fun isleap(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    fun maxDayOfMonth(): Int {
        return mdays[month-1] + if (month == 2 && isleap(year)) 1 else 0
    }

    fun nextDay(): MyCalendar {
        val newCalendar = MyCalendar(year, month, day)
        newCalendar.day++
        if (newCalendar.day > maxDayOfMonth()) {
            newCalendar.day = 1
            newCalendar.month++
        }
        if (newCalendar.month > 12) {
            newCalendar.month = 1
            newCalendar.year++
        }

        return newCalendar
    }

    fun previousDay(): MyCalendar {
        val newCalendar = MyCalendar(year, month, day)
        newCalendar.day--
        if (newCalendar.day < 1) {
            newCalendar.month--
            if (newCalendar.month < 1) {
                newCalendar.month = 12
                newCalendar.year--
            }

            newCalendar.day = maxDayOfMonth()
        }

        return newCalendar
    }
}