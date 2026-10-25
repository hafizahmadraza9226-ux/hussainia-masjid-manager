package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "mosques")
data class MosqueEntity(
  @PrimaryKey val id: String,
  val name: String,
  val shortName: String,
  val code: String, // Prefix e.g. "HMR"
  val address: String,
  val city: String,
  val country: String = "پاکستان",
  val phone: String,
  val email: String = "",
  val currency: String = "PKR",
  val receiptNote: String = "اللہ تعالیٰ آپ کے تعاون کو قبول فرمائے اور آپ کے لیے اجرِ عظیم کا ذریعہ بنائے۔ آمین۔",
  val smsSenderName: String = "Masjid",
  val smsTemplate: String = "جامع مسجد {مسجد کا نام} میں مالی تعاون کرنے کا بہت شکریہ۔ آپ کی رقم مبلغ {رقم} روپے وصول کر لی گئی ہے۔ رسید نمبر: {رسید نمبر}، تاریخ: {تاریخ}، مقصد: {مقصد}۔ جزاک اللہ خیراً۔",
  val smsGatewayApiKey: String = "",
  val smsGatewaySenderId: String = "",
  val logoUri: String? = null,
  val isDefaultInitial: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "mosque_members",
  indices = [Index(value = ["mosqueId", "userEmail"], unique = true)]
)
data class MosqueMemberEntity(
  @PrimaryKey val id: String,
  val mosqueId: String,
  val userId: String,
  val userName: String,
  val userEmail: String,
  val userPhone: String,
  val role: UserRole,
  val inviteCode: String = "",
  val isConfirmed: Boolean = true,
  val joinedAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "incomes",
  indices = [Index("mosqueId"), Index("receiptNumber")]
)
data class IncomeEntity(
  @PrimaryKey val id: String,
  val mosqueId: String,
  val amount: Double,
  val category: IncomeCategory,
  val donorName: String,
  val donorPhone: String = "",
  val donorAddress: String = "",
  val purpose: String = "",
  val paymentMethod: PaymentMethod = PaymentMethod.CASH,
  val receiptNumber: String,
  val recordedByUserName: String,
  val status: TransactionStatus = TransactionStatus.CONFIRMED,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val isSynced: Boolean = true
)

@Entity(
  tableName = "donors",
  indices = [Index("mosqueId")]
)
data class DonorEntity(
  @PrimaryKey val id: String,
  val mosqueId: String,
  val name: String,
  val phone: String,
  val address: String = "",
  val pledgedMonthlyAmount: Double,
  val active: Boolean = true,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "receipts",
  indices = [Index("mosqueId"), Index("receiptNumber", unique = true)]
)
data class ReceiptEntity(
  @PrimaryKey val id: String,
  val mosqueId: String,
  val receiptNumber: String,
  val incomeId: String? = null,
  val donorName: String,
  val donorPhone: String = "",
  val donorAddress: String = "",
  val amount: Double,
  val amountInWords: String,
  val purpose: String,
  val categoryUrdu: String,
  val paymentMethodUrdu: String,
  val issuedBy: String,
  val issueDate: Long = System.currentTimeMillis(),
  val status: TransactionStatus = TransactionStatus.CONFIRMED,
  val smsSent: Boolean = false,
  val notes: String = ""
)

@Entity(
  tableName = "expenses",
  indices = [Index("mosqueId")]
)
data class ExpenseEntity(
  @PrimaryKey val id: String,
  val mosqueId: String,
  val amount: Double,
  val category: ExpenseCategory,
  val payeeName: String, // دکاندار یا وصول کنندہ
  val billReceiptRef: String = "",
  val paymentMethod: PaymentMethod = PaymentMethod.CASH,
  val details: String = "",
  val recordedByUserName: String,
  val status: TransactionStatus = TransactionStatus.CONFIRMED,
  val notes: String = "",
  val date: Long = System.currentTimeMillis(),
  val isSynced: Boolean = true
)

@Entity(
  tableName = "utility_bills",
  indices = [Index("mosqueId")]
)
data class UtilityBillEntity(
  @PrimaryKey val id: String,
  val mosqueId: String,
  val utilityType: ExpenseCategory, // ELECTRICITY, GAS, WATER
  val companyName: String, // e.g. LESCO, SNGPL, WASA
  val consumerNumber: String, // ریفرنس / کنزیومر نمبر
  val billingMonth: String, // e.g. "اکتوبر 2026"
  val issueDate: Long,
  val dueDate: Long,
  val amount: Double,
  val surcharge: Double = 0.0,
  val paidAmount: Double = 0.0,
  val paidDate: Long? = null,
  val status: BillStatus = BillStatus.UNPAID,
  val notes: String = ""
)

@Entity(
  tableName = "salaries",
  indices = [Index("mosqueId")]
)
data class SalaryEntity(
  @PrimaryKey val id: String,
  val mosqueId: String,
  val staffType: StaffType,
  val staffName: String,
  val monthlySalary: Double,
  val monthYear: String,
  val paidAmount: Double,
  val remainingAmount: Double,
  val paymentDate: Long = System.currentTimeMillis(),
  val paymentMethod: PaymentMethod = PaymentMethod.CASH,
  val notes: String = ""
)

@Entity(
  tableName = "budgets",
  indices = [Index(value = ["mosqueId", "periodType", "periodName", "category"], unique = true)]
)
data class BudgetEntity(
  @PrimaryKey val id: String,
  val mosqueId: String,
  val periodType: BudgetPeriod,
  val periodName: String, // e.g. "2026-10" or "2026"
  val category: ExpenseCategory,
  val budgetedAmount: Double,
  val notes: String = ""
)

@Entity(
  tableName = "meetings",
  indices = [Index("mosqueId")]
)
data class MeetingEntity(
  @PrimaryKey val id: String,
  val mosqueId: String,
  val meetingNumber: String,
  val date: Long = System.currentTimeMillis(),
  val venue: String,
  val attendees: String,
  val agenda: String,
  val decisions: String,
  val assignedPerson: String = "",
  val targetDate: Long? = null,
  val status: MeetingStatus = MeetingStatus.COMPLETED,
  val notes: String = ""
)

@Entity(
  tableName = "sms_logs",
  indices = [Index("mosqueId")]
)
data class SmsLogEntity(
  @PrimaryKey val id: String,
  val mosqueId: String,
  val receiptNumber: String,
  val recipientPhone: String,
  val messageBody: String,
  val sentAt: Long = System.currentTimeMillis(),
  val status: SmsDeliveryStatus,
  val errorMessage: String? = null
)

@Entity(
  tableName = "audit_logs",
  indices = [Index("mosqueId")]
)
data class AuditLogEntity(
  @PrimaryKey val id: String,
  val mosqueId: String,
  val userName: String,
  val action: String,
  val details: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
  @PrimaryKey val id: Int = 1,
  val currentActiveMosqueId: String = "",
  val currentActiveUserId: String = "",
  val currentActiveUserName: String = "",
  val currentActiveUserEmail: String = "",
  val currentActiveUserRole: UserRole = UserRole.SUPER_ADMIN,
  val isDarkMode: Boolean = false,
  val useSystemTheme: Boolean = true
)
