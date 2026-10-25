package com.example

import com.example.util.DateUtils
import com.example.util.UrduNumberHelper
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testUrduWordsConversion() {
    val fiveThousand = UrduNumberHelper.convertToUrduWords(5000.0)
    assertTrue("Should contain five thousand in Urdu", fiveThousand.contains("پانچ ہزار"))

    val zero = UrduNumberHelper.convertToUrduWords(0.0)
    assertEquals("صفر روپے فقط", zero)

    val formatted = UrduNumberHelper.formatAmount(50000.0)
    assertEquals("Rs. 50,000", formatted)
  }

  @Test
  fun testDateUtils() {
    val monthUrdu = DateUtils.getCurrentMonthUrdu()
    assertTrue("Month should not be blank", monthUrdu.isNotBlank())
    val year = DateUtils.getCurrentYear()
    assertTrue("Year should be valid", year >= 2026)
  }
}
