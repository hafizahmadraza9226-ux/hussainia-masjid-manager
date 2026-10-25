package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.BillStatus
import com.example.data.model.BudgetPeriod
import com.example.data.model.ExpenseCategory
import com.example.data.model.IncomeCategory
import com.example.data.model.MeetingStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.SmsDeliveryStatus
import com.example.data.model.StaffType
import com.example.data.model.TransactionStatus
import com.example.data.model.UserRole

class Converters {
  @TypeConverter
  fun fromUserRole(value: UserRole?): String? = value?.name

  @TypeConverter
  fun toUserRole(value: String?): UserRole? = value?.let { enumValueOf<UserRole>(it) }

  @TypeConverter
  fun fromIncomeCategory(value: IncomeCategory?): String? = value?.name

  @TypeConverter
  fun toIncomeCategory(value: String?): IncomeCategory? = value?.let { enumValueOf<IncomeCategory>(it) }

  @TypeConverter
  fun fromExpenseCategory(value: ExpenseCategory?): String? = value?.name

  @TypeConverter
  fun toExpenseCategory(value: String?): ExpenseCategory? = value?.let { enumValueOf<ExpenseCategory>(it) }

  @TypeConverter
  fun fromPaymentMethod(value: PaymentMethod?): String? = value?.name

  @TypeConverter
  fun toPaymentMethod(value: String?): PaymentMethod? = value?.let { enumValueOf<PaymentMethod>(it) }

  @TypeConverter
  fun fromTransactionStatus(value: TransactionStatus?): String? = value?.name

  @TypeConverter
  fun toTransactionStatus(value: String?): TransactionStatus? = value?.let { enumValueOf<TransactionStatus>(it) }

  @TypeConverter
  fun fromBillStatus(value: BillStatus?): String? = value?.name

  @TypeConverter
  fun toBillStatus(value: String?): BillStatus? = value?.let { enumValueOf<BillStatus>(it) }

  @TypeConverter
  fun fromStaffType(value: StaffType?): String? = value?.name

  @TypeConverter
  fun toStaffType(value: String?): StaffType? = value?.let { enumValueOf<StaffType>(it) }

  @TypeConverter
  fun fromBudgetPeriod(value: BudgetPeriod?): String? = value?.name

  @TypeConverter
  fun toBudgetPeriod(value: String?): BudgetPeriod? = value?.let { enumValueOf<BudgetPeriod>(it) }

  @TypeConverter
  fun fromMeetingStatus(value: MeetingStatus?): String? = value?.name

  @TypeConverter
  fun toMeetingStatus(value: String?): MeetingStatus? = value?.let { enumValueOf<MeetingStatus>(it) }

  @TypeConverter
  fun fromSmsDeliveryStatus(value: SmsDeliveryStatus?): String? = value?.name

  @TypeConverter
  fun toSmsDeliveryStatus(value: String?): SmsDeliveryStatus? = value?.let { enumValueOf<SmsDeliveryStatus>(it) }
}
