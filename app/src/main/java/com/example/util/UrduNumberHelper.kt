package com.example.util

import java.text.NumberFormat
import java.util.Locale

object UrduNumberHelper {
  private val ones = arrayOf(
    "", "ایک", "دو", "تین", "چار", "پانچ", "چھ", "سات", "آٹھ", "نو", "دس",
    "گیارہ", "بارہ", "تیرہ", "چودہ", "پندرہ", "سولہ", "سترہ", "اٹھارہ", "انیس", "بیس",
    "اکیس", "بائیس", "تیئیس", "چوبیس", "پچیس", "چھبیس", "ستائیس", "اٹھائیس", "انتیس", "تیس",
    "اکتیس", "بتیس", "تینتیس", "چونتیس", "پینتیس", "چھتیس", "سینتیس", "اڑتیس", "انتالیس", "چالیس",
    "اکتالیس", "بیالیس", "تینتالیس", "چوالیس", "پینتالیس", "چھیاسلیس", "سینتالیس", "اڑتالیس", "انچاس", "پچاس",
    "اکیاون", "باون", "ترپن", "چون", "پچپن", "چھپن", "ستاون", "اٹھاون", "انسٹھ", "ساٹھ",
    "اکسٹھ", "باسٹھ", "تریسٹھ", "چونسٹھ", "پینسٹھ", "چھیاسٹھ", "ستاسٹھ", "اٹھاسٹھ", "انتر", "ستر",
    "اکہتر", "بہتر", "تہتر", "چوہتر", "پچہتر", "چھہتر", "ستتر", "اٹھہتر", "اناسی", "اسی",
    "اکیاسی", "بیاسی", "تراسی", "چوراسی", "پچاسی", "چھیاسی", "ستاسی", "اٹھاسی", "نواسی", "نوے",
    "اکیانوے", "بانوے", "ترانوے", "چورانوے", "پچانوے", "چھانوے", "ستانوے", "اٹھانوے", "ننانوے"
  )

  fun formatAmount(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return "Rs. " + formatter.format(amount.toLong())
  }

  fun formatCurrencyUrdu(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return formatter.format(amount.toLong()) + " روپے"
  }

  fun convertToUrduWords(amount: Double): String {
    val num = amount.toLong()
    if (num == 0L) return "صفر روپے فقط"
    if (num < 0) return "منفی " + convertToUrduWords(-amount)

    val words = convertNumber(num).trim()
    return "$words روپے فقط"
  }

  private fun convertNumber(n: Long): String {
    if (n == 0L) return ""
    if (n >= 10000000L) { // Crore
      val crores = n / 10000000L
      val rem = n % 10000000L
      return "${convertNumber(crores)} کروڑ ${convertNumber(rem)}"
    }
    if (n >= 100000L) { // Lakh
      val lakhs = n / 100000L
      val rem = n % 100000L
      return "${convertNumber(lakhs)} لاکھ ${convertNumber(rem)}"
    }
    if (n >= 1000L) { // Hazar
      val thousands = n / 1000L
      val rem = n % 1000L
      return "${convertNumber(thousands)} ہزار ${convertNumber(rem)}"
    }
    if (n >= 100L) { // Sau
      val hundreds = n / 100L
      val rem = n % 100L
      val hundredWord = when (hundreds) {
        1L -> "ایک سو"
        2L -> "دو سو"
        3L -> "تین سو"
        4L -> "چار سو"
        5L -> "پانچ سو"
        6L -> "چھ سو"
        7L -> "سات سو"
        8L -> "آٹھ سو"
        9L -> "نو سو"
        else -> "${convertNumber(hundreds)} سو"
      }
      return "$hundredWord ${convertNumber(rem)}"
    }
    val intVal = n.toInt()
    if (intVal in 1..99) {
      return ones[intVal]
    }
    return n.toString()
  }
}
