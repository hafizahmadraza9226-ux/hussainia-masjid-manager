package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.MasjidRepository
import com.example.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
  val todayIncome: Double = 0.0,
  val todayExpense: Double = 0.0,
  val monthIncome: Double = 0.0,
  val monthExpense: Double = 0.0,
  val totalIncome: Double = 0.0,
  val totalExpense: Double = 0.0,
  val netBalance: Double = 0.0,
  val pendingBillsCount: Int = 0,
  val unpaidBillsTotal: Double = 0.0
)

class MasjidViewModel(application: Application) : AndroidViewModel(application) {
  val repository = MasjidRepository(application)

  val settings = repository.observeSettings()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val allMosques = repository.getAllMosques()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _activeMosqueId = MutableStateFlow("")
  val activeMosqueId: StateFlow<String> = _activeMosqueId.asStateFlow()

  @OptIn(ExperimentalCoroutinesApi::class)
  val activeMosque: StateFlow<MosqueEntity?> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(null) else repository.observeMosque(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  @OptIn(ExperimentalCoroutinesApi::class)
  val incomes: StateFlow<List<IncomeEntity>> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(emptyList()) else repository.getIncomes(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val receipts: StateFlow<List<ReceiptEntity>> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(emptyList()) else repository.getReceipts(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val expenses: StateFlow<List<ExpenseEntity>> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(emptyList()) else repository.getExpenses(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val donors: StateFlow<List<DonorEntity>> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(emptyList()) else repository.getDonors(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val utilityBills: StateFlow<List<UtilityBillEntity>> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(emptyList()) else repository.getUtilityBills(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val salaries: StateFlow<List<SalaryEntity>> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(emptyList()) else repository.getSalaries(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val budgets: StateFlow<List<BudgetEntity>> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(emptyList()) else repository.getBudgets(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val meetings: StateFlow<List<MeetingEntity>> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(emptyList()) else repository.getMeetings(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val members: StateFlow<List<MosqueMemberEntity>> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(emptyList()) else repository.getMembersForMosque(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val smsLogs: StateFlow<List<SmsLogEntity>> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(emptyList()) else repository.getSmsLogs(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val auditLogs: StateFlow<List<AuditLogEntity>> = _activeMosqueId
    .flatMapLatest { id ->
      if (id.isBlank()) flowOf(emptyList()) else repository.getAuditLogs(id)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val dashboardState: StateFlow<DashboardUiState> = combine(
    incomes, expenses, utilityBills
  ) { incs, exps, bills ->
    val startOfDay = DateUtils.getStartOfDay()
    val startOfMonth = DateUtils.getStartOfMonth()

    val confirmedIncs = incs.filter { it.status == TransactionStatus.CONFIRMED }
    val confirmedExps = exps.filter { it.status == TransactionStatus.CONFIRMED }

    val totalInc = confirmedIncs.sumOf { it.amount }
    val totalExp = confirmedExps.sumOf { it.amount }

    val todayInc = confirmedIncs.filter { it.createdAt >= startOfDay }.sumOf { it.amount }
    val todayExp = confirmedExps.filter { it.date >= startOfDay }.sumOf { it.amount }

    val monthInc = confirmedIncs.filter { it.createdAt >= startOfMonth }.sumOf { it.amount }
    val monthExp = confirmedExps.filter { it.date >= startOfMonth }.sumOf { it.amount }

    val unpaidBills = bills.filter { it.status != BillStatus.PAID }
    val unpaidTotal = unpaidBills.sumOf { it.amount - it.paidAmount }

    DashboardUiState(
      todayIncome = todayInc,
      todayExpense = todayExp,
      monthIncome = monthInc,
      monthExpense = monthExp,
      totalIncome = totalInc,
      totalExpense = totalExp,
      netBalance = totalInc - totalExp,
      pendingBillsCount = unpaidBills.size,
      unpaidBillsTotal = unpaidTotal
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

  init {
    viewModelScope.launch {
      repository.checkAndInitializeDefaultMosque()
      val s = repository.getSettings()
      if (s.currentActiveMosqueId.isNotBlank()) {
        _activeMosqueId.value = s.currentActiveMosqueId
      } else {
        allMosques.firstOrNull { it.isNotEmpty() }?.firstOrNull()?.let {
          _activeMosqueId.value = it.id
        }
      }
    }
  }

  fun switchActiveMosque(mosqueId: String) {
    viewModelScope.launch {
      repository.switchActiveMosque(mosqueId)
      _activeMosqueId.value = mosqueId
    }
  }

  fun createNewMosque(
    name: String,
    shortName: String,
    code: String,
    address: String,
    city: String,
    phone: String,
    email: String,
    adminName: String,
    adminEmail: String,
    adminPhone: String,
    onSuccess: (String) -> Unit
  ) {
    viewModelScope.launch {
      val newId = repository.createNewMosque(
        name = name,
        shortName = shortName,
        code = code,
        address = address,
        city = city,
        phone = phone,
        email = email,
        adminName = adminName,
        adminEmail = adminEmail,
        adminPhone = adminPhone
      )
      _activeMosqueId.value = newId
      onSuccess(newId)
    }
  }

  fun joinMosque(
    inviteCode: String,
    name: String,
    email: String,
    phone: String,
    onResult: (Result<String>) -> Unit
  ) {
    viewModelScope.launch {
      val res = repository.joinMosqueViaInviteCode(inviteCode, name, email, phone)
      if (res.isSuccess) {
        val s = repository.getSettings()
        _activeMosqueId.value = s.currentActiveMosqueId
      }
      onResult(res)
    }
  }

  fun updateMosqueProfile(updated: MosqueEntity) {
    viewModelScope.launch {
      repository.updateMosque(updated)
    }
  }

  fun recordIncome(
    amount: Double,
    category: IncomeCategory,
    donorName: String,
    donorPhone: String,
    donorAddress: String,
    purpose: String,
    paymentMethod: PaymentMethod,
    notes: String,
    sendSmsImmediately: Boolean,
    onSuccess: (ReceiptEntity) -> Unit
  ) {
    viewModelScope.launch {
      val currentMosqueId = _activeMosqueId.value
      val currentUserName = settings.value?.currentActiveUserName ?: "ایڈمن"
      val receipt = repository.recordIncomeAndGenerateReceipt(
        mosqueId = currentMosqueId,
        amount = amount,
        category = category,
        donorName = donorName,
        donorPhone = donorPhone,
        donorAddress = donorAddress,
        purpose = purpose,
        paymentMethod = paymentMethod,
        userName = currentUserName,
        notes = notes,
        sendSmsImmediately = sendSmsImmediately
      )
      onSuccess(receipt)
    }
  }

  fun cancelReceipt(receipt: ReceiptEntity) {
    viewModelScope.launch {
      val currentUserName = settings.value?.currentActiveUserName ?: "ایڈمن"
      repository.cancelReceipt(receipt, currentUserName)
    }
  }

  fun recordExpense(
    amount: Double,
    category: ExpenseCategory,
    payeeName: String,
    billReceiptRef: String,
    paymentMethod: PaymentMethod,
    details: String,
    notes: String
  ) {
    viewModelScope.launch {
      val currentMosqueId = _activeMosqueId.value
      val currentUserName = settings.value?.currentActiveUserName ?: "ایڈمن"
      repository.recordExpense(
        mosqueId = currentMosqueId,
        amount = amount,
        category = category,
        payeeName = payeeName,
        billReceiptRef = billReceiptRef,
        paymentMethod = paymentMethod,
        details = details,
        userName = currentUserName,
        notes = notes
      )
    }
  }

  fun saveDonor(donor: DonorEntity) {
    viewModelScope.launch {
      repository.addDonor(donor)
    }
  }

  fun deleteDonor(donor: DonorEntity) {
    viewModelScope.launch {
      repository.deleteDonor(donor)
    }
  }

  fun saveUtilityBill(bill: UtilityBillEntity) {
    viewModelScope.launch {
      repository.addUtilityBill(bill)
    }
  }

  fun updateUtilityBill(bill: UtilityBillEntity) {
    viewModelScope.launch {
      repository.updateUtilityBill(bill)
    }
  }

  fun deleteUtilityBill(bill: UtilityBillEntity) {
    viewModelScope.launch {
      repository.deleteUtilityBill(bill)
    }
  }

  fun saveSalary(salary: SalaryEntity) {
    viewModelScope.launch {
      repository.addSalary(salary)
    }
  }

  fun saveBudget(budget: BudgetEntity) {
    viewModelScope.launch {
      repository.setBudget(budget)
    }
  }

  fun saveMeeting(meeting: MeetingEntity) {
    viewModelScope.launch {
      repository.addMeeting(meeting)
    }
  }

  fun updateMeeting(meeting: MeetingEntity) {
    viewModelScope.launch {
      repository.updateMeeting(meeting)
    }
  }

  fun toggleTheme(isDark: Boolean, useSystem: Boolean) {
    viewModelScope.launch {
      val s = repository.getSettings()
      repository.updateSettings(s.copy(isDarkMode = isDark, useSystemTheme = useSystem))
    }
  }
}
