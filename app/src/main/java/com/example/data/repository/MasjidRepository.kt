package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.util.DateUtils
import com.example.util.UrduNumberHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class MasjidRepository(context: Context) {
  private val db = AppDatabase.getDatabase(context)
  private val mosqueDao = db.mosqueDao()
  private val memberDao = db.mosqueMemberDao()
  private val incomeDao = db.incomeDao()
  private val donorDao = db.donorDao()
  private val receiptDao = db.receiptDao()
  private val expenseDao = db.expenseDao()
  private val utilityBillDao = db.utilityBillDao()
  private val salaryDao = db.salaryDao()
  private val budgetDao = db.budgetDao()
  private val meetingDao = db.meetingDao()
  private val smsLogDao = db.smsLogDao()
  private val auditLogDao = db.auditLogDao()
  private val appSettingsDao = db.appSettingsDao()

  // App Settings & Active Mosque
  fun observeSettings(): Flow<AppSettingsEntity?> = appSettingsDao.observeSettings()

  suspend fun getSettings(): AppSettingsEntity {
    var settings = appSettingsDao.getSettings()
    if (settings == null) {
      settings = AppSettingsEntity(
        currentActiveMosqueId = "",
        currentActiveUserId = "user_super_admin",
        currentActiveUserName = "سپر ایڈمن",
        currentActiveUserEmail = "admin@masjid.pk",
        currentActiveUserRole = UserRole.SUPER_ADMIN,
        isDarkMode = false,
        useSystemTheme = false
      )
      appSettingsDao.insertOrUpdate(settings)
    }
    return settings
  }

  suspend fun updateSettings(settings: AppSettingsEntity) {
    appSettingsDao.insertOrUpdate(settings)
  }

  // Mosques
  fun getAllMosques(): Flow<List<MosqueEntity>> = mosqueDao.getAllMosques()

  fun observeMosque(id: String): Flow<MosqueEntity?> = mosqueDao.observeMosqueById(id)

  suspend fun getMosque(id: String): MosqueEntity? = mosqueDao.getMosqueById(id)

  suspend fun checkAndInitializeDefaultMosque() {
    val count = mosqueDao.getMosqueCount()
    if (count == 0) {
      val defaultMosque = MosqueEntity(
        id = "hussainia_adda_wali",
        name = "جامع مسجد حسینیہ رضویہ المعروف اڈے والی مسجد",
        shortName = "جامع مسجد حسینیہ",
        code = "HMR",
        address = "مین اڈا لاری، نزد جی ٹی روڈ",
        city = "لاہور",
        country = "پاکستان",
        phone = "0300-7654321",
        email = "hussainia.masjid@gmail.com",
        currency = "PKR",
        receiptNote = "اللہ تعالیٰ آپ کے مالی تعاون کو شرفِ قبولیت بخشے اور دونوں جہانوں میں اجرِ عظیم عطا فرمائے۔ آمین۔",
        smsSenderName = "HMR-Masjid",
        isDefaultInitial = true
      )
      mosqueDao.insertMosque(defaultMosque)

      // Add default 4-role members
      val members = listOf(
        MosqueMemberEntity(
          id = UUID.randomUUID().toString(),
          mosqueId = defaultMosque.id,
          userId = "user_1",
          userName = "حافظ احمد رضا (سپر ایڈمن)",
          userEmail = "hafizahmadraza9226@gmail.com",
          userPhone = "0300-1112233",
          role = UserRole.SUPER_ADMIN,
          inviteCode = "HMR-ADM"
        ),
        MosqueMemberEntity(
          id = UUID.randomUUID().toString(),
          mosqueId = defaultMosque.id,
          userId = "user_2",
          userName = "محمد عثمان (فنانس مینیجر)",
          userEmail = "finance@hussainia.pk",
          userPhone = "0321-4455667",
          role = UserRole.FINANCE_MANAGER,
          inviteCode = "HMR-FIN"
        ),
        MosqueMemberEntity(
          id = UUID.randomUUID().toString(),
          mosqueId = defaultMosque.id,
          userId = "user_3",
          userName = "قاری عبدالرشید (ریکارڈ مینیجر)",
          userEmail = "records@hussainia.pk",
          userPhone = "0333-7788990",
          role = UserRole.RECORD_MANAGER,
          inviteCode = "HMR-REC"
        ),
        MosqueMemberEntity(
          id = UUID.randomUUID().toString(),
          mosqueId = defaultMosque.id,
          userId = "user_4",
          userName = "چوہدری بشیر احمد (ناظر)",
          userEmail = "review@hussainia.pk",
          userPhone = "0345-9988776",
          role = UserRole.READ_REVIEW,
          inviteCode = "HMR-REV"
        )
      )
      members.forEach { memberDao.insertMember(it) }

      // Seed initial sample monthly budget & salaries for the default mosque
      val currentMonth = DateUtils.getCurrentMonthUrdu()
      salaryDao.insertSalary(
        SalaryEntity(
          id = UUID.randomUUID().toString(),
          mosqueId = defaultMosque.id,
          staffType = StaffType.IMAM,
          staffName = "مولانا حافظ احمد رضا صاحب",
          monthlySalary = 45000.0,
          monthYear = currentMonth,
          paidAmount = 45000.0,
          remainingAmount = 0.0,
          paymentDate = System.currentTimeMillis() - 86400000L * 5,
          paymentMethod = PaymentMethod.BANK_TRANSFER,
          notes = "بروقت ادائیگی بابت ماہانہ خدمات"
        )
      )
      salaryDao.insertSalary(
        SalaryEntity(
          id = UUID.randomUUID().toString(),
          mosqueId = defaultMosque.id,
          staffType = StaffType.MOAZZIN,
          staffName = "قاری محمد بلال صاحب",
          monthlySalary = 28000.0,
          monthYear = currentMonth,
          paidAmount = 28000.0,
          remainingAmount = 0.0,
          paymentDate = System.currentTimeMillis() - 86400000L * 5,
          paymentMethod = PaymentMethod.CASH,
          notes = "بروقت نقد ادائیگی"
        )
      )

      // Set active mosque in settings
      val currentSettings = getSettings()
      appSettingsDao.insertOrUpdate(
        currentSettings.copy(
          currentActiveMosqueId = defaultMosque.id,
          currentActiveUserName = "حافظ احمد رضا (سپر ایڈمن)",
          currentActiveUserEmail = "hafizahmadraza9226@gmail.com",
          currentActiveUserRole = UserRole.SUPER_ADMIN
        )
      )

      // Log initial creation
      auditLogDao.insertAuditLog(
        AuditLogEntity(
          id = UUID.randomUUID().toString(),
          mosqueId = defaultMosque.id,
          userName = "سسٹم",
          action = "ابتدائی مسجد سیٹ اپ",
          details = "جامع مسجد حسینیہ رضویہ کا ورکسپیس کامیابی سے بنایا گیا۔"
        )
      )
    }
  }

  suspend fun createNewMosque(
    name: String,
    shortName: String,
    code: String,
    address: String,
    city: String,
    phone: String,
    email: String,
    adminName: String,
    adminEmail: String,
    adminPhone: String
  ): String {
    val newId = "mosque_" + UUID.randomUUID().toString().take(8)
    val upperCode = if (code.isNotBlank()) code.uppercase().trim() else "MSQ"

    val newMosque = MosqueEntity(
      id = newId,
      name = name.trim(),
      shortName = if (shortName.isNotBlank()) shortName.trim() else name.take(15).trim(),
      code = upperCode,
      address = address.trim(),
      city = city.trim(),
      phone = phone.trim(),
      email = email.trim(),
      isDefaultInitial = false
    )
    mosqueDao.insertMosque(newMosque)

    // Creator is Super Admin
    val superAdminMember = MosqueMemberEntity(
      id = UUID.randomUUID().toString(),
      mosqueId = newId,
      userId = "user_" + UUID.randomUUID().toString().take(6),
      userName = adminName.ifBlank { "ایڈمن" },
      userEmail = adminEmail.ifBlank { "admin@${upperCode.lowercase()}.pk" },
      userPhone = adminPhone,
      role = UserRole.SUPER_ADMIN,
      inviteCode = "${upperCode}-ADM"
    )
    memberDao.insertMember(superAdminMember)

    // Pre-generate invite codes for the remaining 3 allowed users
    val remainingRoles = listOf(
      UserRole.FINANCE_MANAGER to "${upperCode}-FIN",
      UserRole.RECORD_MANAGER to "${upperCode}-REC",
      UserRole.READ_REVIEW to "${upperCode}-REV"
    )
    remainingRoles.forEachIndexed { idx, (role, codeSuffix) ->
      memberDao.insertMember(
        MosqueMemberEntity(
          id = UUID.randomUUID().toString(),
          mosqueId = newId,
          userId = "pending_$idx",
          userName = "دعوت یافتہ رکن (${role.urduTitle})",
          userEmail = "invite_$idx@${upperCode.lowercase()}.pk",
          userPhone = "",
          role = role,
          inviteCode = codeSuffix,
          isConfirmed = false
        )
      )
    }

    // Switch active workspace to this new mosque
    val currentSettings = getSettings()
    appSettingsDao.insertOrUpdate(
      currentSettings.copy(
        currentActiveMosqueId = newId,
        currentActiveUserName = superAdminMember.userName,
        currentActiveUserEmail = superAdminMember.userEmail,
        currentActiveUserRole = UserRole.SUPER_ADMIN
      )
    )

    auditLogDao.insertAuditLog(
      AuditLogEntity(
        id = UUID.randomUUID().toString(),
        mosqueId = newId,
        userName = adminName,
        action = "نئی مسجد تخلیق",
        details = "نئی مسجد '$name' ($upperCode) کا الگ ورکسپیس بنایا گیا۔"
      )
    )
    return newId
  }

  suspend fun updateMosque(mosque: MosqueEntity) {
    mosqueDao.updateMosque(mosque)
  }

  // Mosque Members
  fun getMembersForMosque(mosqueId: String): Flow<List<MosqueMemberEntity>> =
    memberDao.getMembersForMosque(mosqueId)

  suspend fun joinMosqueViaInviteCode(
    inviteCode: String,
    userName: String,
    userEmail: String,
    userPhone: String
  ): Result<String> {
    val cleanCode = inviteCode.trim().uppercase()
    val member = memberDao.getMemberByInviteCode(cleanCode)
      ?: return Result.failure(Exception("درج کردہ دعوتی کوڈ درست نہیں ہے"))

    // Update member record to active
    val updated = member.copy(
      userName = userName,
      userEmail = userEmail,
      userPhone = userPhone,
      isConfirmed = true,
      joinedAt = System.currentTimeMillis()
    )
    memberDao.updateMember(updated)

    // Switch active workspace
    val currentSettings = getSettings()
    appSettingsDao.insertOrUpdate(
      currentSettings.copy(
        currentActiveMosqueId = member.mosqueId,
        currentActiveUserName = userName,
        currentActiveUserEmail = userEmail,
        currentActiveUserRole = member.role
      )
    )

    val mosque = mosqueDao.getMosqueById(member.mosqueId)
    val mosqueName = mosque?.name ?: "مسجد"

    auditLogDao.insertAuditLog(
      AuditLogEntity(
        id = UUID.randomUUID().toString(),
        mosqueId = member.mosqueId,
        userName = userName,
        action = "دعوتی کوڈ سے شمولیت",
        details = "$userName نے بطور ${member.role.urduTitle} مسجد میں شمولیت اختیار کی۔"
      )
    )

    return Result.success(mosqueName)
  }

  suspend fun switchActiveMosque(mosqueId: String) {
    val mosque = mosqueDao.getMosqueById(mosqueId) ?: return
    val currentSettings = getSettings()
    val members = memberDao.getMembersForMosque(mosqueId).firstOrNull() ?: emptyList()
    val activeMember = members.firstOrNull { it.isConfirmed }

    appSettingsDao.insertOrUpdate(
      currentSettings.copy(
        currentActiveMosqueId = mosqueId,
        currentActiveUserName = activeMember?.userName ?: currentSettings.currentActiveUserName,
        currentActiveUserEmail = activeMember?.userEmail ?: currentSettings.currentActiveUserEmail,
        currentActiveUserRole = activeMember?.role ?: UserRole.SUPER_ADMIN
      )
    )
  }

  // Incomes & Receipts
  fun getIncomes(mosqueId: String): Flow<List<IncomeEntity>> =
    incomeDao.getIncomesForMosque(mosqueId)

  fun getTotalConfirmedIncome(mosqueId: String): Flow<Double?> =
    incomeDao.getTotalConfirmedIncome(mosqueId)

  fun getConfirmedIncomeSince(mosqueId: String, timestamp: Long): Flow<Double?> =
    incomeDao.getConfirmedIncomeSince(mosqueId, timestamp)

  suspend fun recordIncomeAndGenerateReceipt(
    mosqueId: String,
    amount: Double,
    category: IncomeCategory,
    donorName: String,
    donorPhone: String,
    donorAddress: String,
    purpose: String,
    paymentMethod: PaymentMethod,
    userName: String,
    notes: String,
    sendSmsImmediately: Boolean = false
  ): ReceiptEntity {
    val mosque = mosqueDao.getMosqueById(mosqueId)
      ?: throw IllegalStateException("مسجد موجود نہیں ہے")

    // Generate unique sequential receipt number: e.g. HMR-2026-000001
    val receiptCount = receiptDao.getReceiptCount(mosqueId) + 1
    val currentYear = DateUtils.getCurrentYear()
    val receiptNumber = "${mosque.code}-$currentYear-${String.format("%06d", receiptCount)}"

    val incomeId = UUID.randomUUID().toString()
    val income = IncomeEntity(
      id = incomeId,
      mosqueId = mosqueId,
      amount = amount,
      category = category,
      donorName = donorName.trim(),
      donorPhone = donorPhone.trim(),
      donorAddress = donorAddress.trim(),
      purpose = purpose.ifBlank { category.urduTitle },
      paymentMethod = paymentMethod,
      receiptNumber = receiptNumber,
      recordedByUserName = userName,
      status = TransactionStatus.CONFIRMED,
      notes = notes.trim(),
      createdAt = System.currentTimeMillis()
    )
    incomeDao.insertIncome(income)

    // Convert amount to Urdu words
    val urduWords = UrduNumberHelper.convertToUrduWords(amount)

    val receipt = ReceiptEntity(
      id = UUID.randomUUID().toString(),
      mosqueId = mosqueId,
      receiptNumber = receiptNumber,
      incomeId = incomeId,
      donorName = donorName.trim(),
      donorPhone = donorPhone.trim(),
      donorAddress = donorAddress.trim(),
      amount = amount,
      amountInWords = urduWords,
      purpose = purpose.ifBlank { category.urduTitle },
      categoryUrdu = category.urduTitle,
      paymentMethodUrdu = paymentMethod.urduTitle,
      issuedBy = userName,
      issueDate = System.currentTimeMillis(),
      status = TransactionStatus.CONFIRMED,
      smsSent = sendSmsImmediately && donorPhone.isNotBlank(),
      notes = notes.trim()
    )
    receiptDao.insertReceipt(receipt)

    // Handle SMS confirmation
    if (donorPhone.isNotBlank()) {
      val formattedDate = DateUtils.formatUrduDate(receipt.issueDate)
      val formattedAmount = UrduNumberHelper.formatAmount(amount)
      val smsText = mosque.smsTemplate
        .replace("{مسجد کا نام}", mosque.name)
        .replace("{رقم}", formattedAmount)
        .replace("{رسید نمبر}", receiptNumber)
        .replace("{تاریخ}", formattedDate)
        .replace("{مقصد}", receipt.purpose)

      val status = if (sendSmsImmediately) {
        if (mosque.smsGatewayApiKey.isNotBlank()) SmsDeliveryStatus.SENT else SmsDeliveryStatus.SENT
      } else {
        SmsDeliveryStatus.QUEUED
      }

      smsLogDao.insertSmsLog(
        SmsLogEntity(
          id = UUID.randomUUID().toString(),
          mosqueId = mosqueId,
          receiptNumber = receiptNumber,
          recipientPhone = donorPhone,
          messageBody = smsText,
          status = status,
          errorMessage = if (mosque.smsGatewayApiKey.isBlank()) "Gateway API کی غیر موجودگی کی وجہ سے ڈیمو لاگ کے طور پر محفوظ کیا گیا۔" else null
        )
      )
    }

    auditLogDao.insertAuditLog(
      AuditLogEntity(
        id = UUID.randomUUID().toString(),
        mosqueId = mosqueId,
        userName = userName,
        action = "رسید کا اجراء",
        details = "رسید نمبر $receiptNumber مبلغ ${UrduNumberHelper.formatAmount(amount)} برائے $donorName جاری کی گئی۔"
      )
    )

    return receipt
  }

  fun getReceipts(mosqueId: String): Flow<List<ReceiptEntity>> =
    receiptDao.getReceiptsForMosque(mosqueId)

  suspend fun cancelReceipt(receipt: ReceiptEntity, userName: String) {
    val updated = receipt.copy(status = TransactionStatus.CANCELLED)
    receiptDao.updateReceipt(updated)

    // Also update associated income
    receipt.incomeId?.let { incId ->
      // Delete or set cancelled
      incomeDao.deleteIncome(incId, receipt.mosqueId)
    }

    auditLogDao.insertAuditLog(
      AuditLogEntity(
        id = UUID.randomUUID().toString(),
        mosqueId = receipt.mosqueId,
        userName = userName,
        action = "رسید کی منسوخی",
        details = "رسید نمبر ${receipt.receiptNumber} منسوخ کر دی گئی۔"
      )
    )
  }

  // Donors
  fun getDonors(mosqueId: String): Flow<List<DonorEntity>> = donorDao.getDonorsForMosque(mosqueId)

  suspend fun addDonor(donor: DonorEntity) {
    donorDao.insertDonor(donor)
  }

  suspend fun updateDonor(donor: DonorEntity) {
    donorDao.updateDonor(donor)
  }

  suspend fun deleteDonor(donor: DonorEntity) {
    donorDao.deleteDonor(donor)
  }

  // Expenses
  fun getExpenses(mosqueId: String): Flow<List<ExpenseEntity>> =
    expenseDao.getExpensesForMosque(mosqueId)

  fun getTotalConfirmedExpense(mosqueId: String): Flow<Double?> =
    expenseDao.getTotalConfirmedExpense(mosqueId)

  fun getConfirmedExpenseSince(mosqueId: String, timestamp: Long): Flow<Double?> =
    expenseDao.getConfirmedExpenseSince(mosqueId, timestamp)

  suspend fun recordExpense(
    mosqueId: String,
    amount: Double,
    category: ExpenseCategory,
    payeeName: String,
    billReceiptRef: String,
    paymentMethod: PaymentMethod,
    details: String,
    userName: String,
    notes: String
  ) {
    val expense = ExpenseEntity(
      id = UUID.randomUUID().toString(),
      mosqueId = mosqueId,
      amount = amount,
      category = category,
      payeeName = payeeName.trim(),
      billReceiptRef = billReceiptRef.trim(),
      paymentMethod = paymentMethod,
      details = details.trim(),
      recordedByUserName = userName,
      status = TransactionStatus.CONFIRMED,
      notes = notes.trim(),
      date = System.currentTimeMillis()
    )
    expenseDao.insertExpense(expense)

    auditLogDao.insertAuditLog(
      AuditLogEntity(
        id = UUID.randomUUID().toString(),
        mosqueId = mosqueId,
        userName = userName,
        action = "خرچ کا اندراج",
        details = "${category.urduTitle} کی مد میں ${UrduNumberHelper.formatAmount(amount)} ادا کیے گئے۔"
      )
    )
  }

  // Utility Bills
  fun getUtilityBills(mosqueId: String): Flow<List<UtilityBillEntity>> =
    utilityBillDao.getUtilityBillsForMosque(mosqueId)

  suspend fun addUtilityBill(bill: UtilityBillEntity) {
    utilityBillDao.insertUtilityBill(bill)
  }

  suspend fun updateUtilityBill(bill: UtilityBillEntity) {
    utilityBillDao.updateUtilityBill(bill)
  }

  suspend fun deleteUtilityBill(bill: UtilityBillEntity) {
    utilityBillDao.deleteUtilityBill(bill)
  }

  // Salaries
  fun getSalaries(mosqueId: String): Flow<List<SalaryEntity>> =
    salaryDao.getSalariesForMosque(mosqueId)

  suspend fun addSalary(salary: SalaryEntity) {
    salaryDao.insertSalary(salary)
  }

  // Budgets
  fun getBudgets(mosqueId: String): Flow<List<BudgetEntity>> =
    budgetDao.getAllBudgets(mosqueId)

  suspend fun setBudget(budget: BudgetEntity) {
    budgetDao.insertBudget(budget)
  }

  // Meetings
  fun getMeetings(mosqueId: String): Flow<List<MeetingEntity>> =
    meetingDao.getMeetingsForMosque(mosqueId)

  suspend fun addMeeting(meeting: MeetingEntity) {
    meetingDao.insertMeeting(meeting)
  }

  suspend fun updateMeeting(meeting: MeetingEntity) {
    meetingDao.updateMeeting(meeting)
  }

  // SMS Logs
  fun getSmsLogs(mosqueId: String): Flow<List<SmsLogEntity>> =
    smsLogDao.getSmsLogsForMosque(mosqueId)

  // Audit Logs
  fun getAuditLogs(mosqueId: String): Flow<List<AuditLogEntity>> =
    auditLogDao.getAuditLogsForMosque(mosqueId)
}
