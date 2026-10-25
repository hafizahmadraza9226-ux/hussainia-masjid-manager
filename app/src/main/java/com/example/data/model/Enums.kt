package com.example.data.model

enum class UserRole(val urduTitle: String, val description: String) {
  SUPER_ADMIN("سپر ایڈمن", "مکمل اختیارات، مسجد سیٹنگز، صارفین اور بیک اپ"),
  FINANCE_MANAGER("فنانس مینیجر", "آمدن، چندہ، رسیدیں، اخراجات، بجٹ اور مالی رپورٹس"),
  RECORD_MANAGER("ریکارڈ مینیجر", "یوٹیلیٹی بل، تنخواہیں، اجلاس، فیصلے اور نوٹس"),
  READ_REVIEW("ناظر / ریویو", "صرف رپورٹس اور ریکارڈ دیکھنے کے مجاز حقوق")
}

enum class IncomeCategory(val urduTitle: String) {
  GENERAL_DONATION("عمومی چندہ"),
  MONTHLY_CONTRIBUTION("ماہانہ تعاون"),
  FRIDAY_COLLECTION("جمعہ چندہ"),
  RAMADAN_COLLECTION("رمضان چندہ"),
  CONSTRUCTION_FUND("تعمیراتی فنڈ"),
  SALARY_FUND("تنخواہوں کے لیے تعاون"),
  OTHER_INCOME("دیگر منظور شدہ آمدن")
}

enum class ExpenseCategory(val urduTitle: String) {
  ELECTRICITY("بجلی کا بل"),
  GAS("سوئی گیس کا بل"),
  WATER("پانی کا بل / واسا"),
  IMAM_SALARY("تنخواہ امام صاحب"),
  MOAZZIN_SALARY("تنخواہ مؤذن صاحب"),
  CLEANING("صفائی ستھرائی و فنائل"),
  CONSTRUCTION("تعمیراتی کام"),
  REPAIRS("مرمت و دیکھ بھال"),
  ELECTRICAL("برقی سامان و وائرنگ"),
  SOLAR_SYSTEM("سولر سسٹم و بیٹری"),
  SECURITY_CCTV("CCTV کیمرے و سیکیورٹی"),
  CARPETS_MATS("قالین، صفیں اور چٹائیاں"),
  OTHER("دیگر مسجدی ضروریات")
}

enum class PaymentMethod(val urduTitle: String) {
  CASH("نقد (Cash)"),
  BANK_TRANSFER("بینک ٹرانسفر"),
  JAZZCASH("جاز کیش (JazzCash)"),
  EASYPAISA("ایزی پیسہ (Easypaisa)"),
  CHEQUE("بینک چیک (Cheque)")
}

enum class TransactionStatus(val urduTitle: String) {
  DRAFT("ڈرافٹ"),
  CONFIRMED("منظور و تصدیق شدہ"),
  CANCELLED("منسوخ شدہ")
}

enum class BillStatus(val urduTitle: String) {
  UNPAID("غیر ادا شدہ"),
  PAID("ادا شدہ"),
  OVERDUE("واجب الادا / تاخیر")
}

enum class StaffType(val urduTitle: String) {
  IMAM("امام و خطیب"),
  MOAZZIN("مؤذن صاحب"),
  KHADIM("خادم مسجد")
}

enum class BudgetPeriod(val urduTitle: String) {
  MONTHLY("ماہانہ بجٹ"),
  ANNUAL("سالانہ بجٹ")
}

enum class MeetingStatus(val urduTitle: String) {
  PENDING("زیرِ غور"),
  IN_PROGRESS("زیرِ تکمیل"),
  COMPLETED("مکمل / نافذ"),
  CANCELLED("منسوخ")
}

enum class SmsDeliveryStatus(val urduTitle: String) {
  SENT("کامیابی سے ارسال"),
  FAILED("ارسال میں ناکامی"),
  QUEUED("انتظار میں (آف لائن)")
}
