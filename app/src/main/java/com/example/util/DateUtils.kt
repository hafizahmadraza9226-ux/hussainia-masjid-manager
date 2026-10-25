package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
  private val urduMonths = arrayOf(
    "جنوری", "فروری", "مارچ", "اپریل", "مئی", "جون",
    "جولائی", "اگست", "ستمبر", "اکتوبر", "نومبر", "دسمبر"
  )

  fun formatUrduDate(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val month = urduMonths[cal.get(Calendar.MONTH)]
    val year = cal.get(Calendar.YEAR)
    return "$day $month $year"
  }

  fun formatUrduDateTime(timestamp: Long): String {
    val datePart = formatUrduDate(timestamp)
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
    val timePart = timeFormat.format(Date(timestamp))
      .replace("AM", "صبح")
      .replace("PM", "شام")
    return "$datePart ($timePart)"
  }

  fun getCurrentMonthUrdu(): String {
    val cal = Calendar.getInstance()
    val month = urduMonths[cal.get(Calendar.MONTH)]
    val year = cal.get(Calendar.YEAR)
    return "$month $year"
  }

  fun getCurrentYear(): Int {
    return Calendar.getInstance().get(Calendar.YEAR)
  }

  fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
    val cal = Calendar.getInstance().apply {
      timeInMillis = timestamp
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
  }

  fun getStartOfMonth(timestamp: Long = System.currentTimeMillis()): Long {
    val cal = Calendar.getInstance().apply {
      timeInMillis = timestamp
      set(Calendar.DAY_OF_MONTH, 1)
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
  }

  fun getStartOfYear(timestamp: Long = System.currentTimeMillis()): Long {
    val cal = Calendar.getInstance().apply {
      timeInMillis = timestamp
      set(Calendar.DAY_OF_YEAR, 1)
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
  }
}
