package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MosqueDao {
  @Query("SELECT * FROM mosques ORDER BY createdAt ASC")
  fun getAllMosques(): Flow<List<MosqueEntity>>

  @Query("SELECT * FROM mosques WHERE id = :id LIMIT 1")
  suspend fun getMosqueById(id: String): MosqueEntity?

  @Query("SELECT * FROM mosques WHERE id = :id LIMIT 1")
  fun observeMosqueById(id: String): Flow<MosqueEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMosque(mosque: MosqueEntity)

  @Update
  suspend fun updateMosque(mosque: MosqueEntity)

  @Delete
  suspend fun deleteMosque(mosque: MosqueEntity)

  @Query("SELECT COUNT(*) FROM mosques")
  suspend fun getMosqueCount(): Int
}

@Dao
interface MosqueMemberDao {
  @Query("SELECT * FROM mosque_members WHERE mosqueId = :mosqueId ORDER BY joinedAt ASC")
  fun getMembersForMosque(mosqueId: String): Flow<List<MosqueMemberEntity>>

  @Query("SELECT COUNT(*) FROM mosque_members WHERE mosqueId = :mosqueId")
  suspend fun getMemberCount(mosqueId: String): Int

  @Query("SELECT * FROM mosque_members WHERE inviteCode = :code LIMIT 1")
  suspend fun getMemberByInviteCode(code: String): MosqueMemberEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMember(member: MosqueMemberEntity)

  @Update
  suspend fun updateMember(member: MosqueMemberEntity)

  @Delete
  suspend fun deleteMember(member: MosqueMemberEntity)
}

@Dao
interface IncomeDao {
  @Query("SELECT * FROM incomes WHERE mosqueId = :mosqueId ORDER BY createdAt DESC")
  fun getIncomesForMosque(mosqueId: String): Flow<List<IncomeEntity>>

  @Query("SELECT SUM(amount) FROM incomes WHERE mosqueId = :mosqueId AND status = 'CONFIRMED'")
  fun getTotalConfirmedIncome(mosqueId: String): Flow<Double?>

  @Query("SELECT SUM(amount) FROM incomes WHERE mosqueId = :mosqueId AND status = 'CONFIRMED' AND createdAt >= :sinceTimestamp")
  fun getConfirmedIncomeSince(mosqueId: String, sinceTimestamp: Long): Flow<Double?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertIncome(income: IncomeEntity)

  @Update
  suspend fun updateIncome(income: IncomeEntity)

  @Query("DELETE FROM incomes WHERE id = :id AND mosqueId = :mosqueId")
  suspend fun deleteIncome(id: String, mosqueId: String)
}

@Dao
interface DonorDao {
  @Query("SELECT * FROM donors WHERE mosqueId = :mosqueId ORDER BY name ASC")
  fun getDonorsForMosque(mosqueId: String): Flow<List<DonorEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDonor(donor: DonorEntity)

  @Update
  suspend fun updateDonor(donor: DonorEntity)

  @Delete
  suspend fun deleteDonor(donor: DonorEntity)
}

@Dao
interface ReceiptDao {
  @Query("SELECT * FROM receipts WHERE mosqueId = :mosqueId ORDER BY issueDate DESC")
  fun getReceiptsForMosque(mosqueId: String): Flow<List<ReceiptEntity>>

  @Query("SELECT * FROM receipts WHERE id = :id AND mosqueId = :mosqueId LIMIT 1")
  suspend fun getReceiptById(id: String, mosqueId: String): ReceiptEntity?

  @Query("SELECT * FROM receipts WHERE receiptNumber = :receiptNumber AND mosqueId = :mosqueId LIMIT 1")
  suspend fun getReceiptByNumber(receiptNumber: String, mosqueId: String): ReceiptEntity?

  @Query("SELECT COUNT(*) FROM receipts WHERE mosqueId = :mosqueId")
  suspend fun getReceiptCount(mosqueId: String): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReceipt(receipt: ReceiptEntity)

  @Update
  suspend fun updateReceipt(receipt: ReceiptEntity)
}

@Dao
interface ExpenseDao {
  @Query("SELECT * FROM expenses WHERE mosqueId = :mosqueId ORDER BY date DESC")
  fun getExpensesForMosque(mosqueId: String): Flow<List<ExpenseEntity>>

  @Query("SELECT SUM(amount) FROM expenses WHERE mosqueId = :mosqueId AND status = 'CONFIRMED'")
  fun getTotalConfirmedExpense(mosqueId: String): Flow<Double?>

  @Query("SELECT SUM(amount) FROM expenses WHERE mosqueId = :mosqueId AND status = 'CONFIRMED' AND date >= :sinceTimestamp")
  fun getConfirmedExpenseSince(mosqueId: String, sinceTimestamp: Long): Flow<Double?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpense(expense: ExpenseEntity)

  @Update
  suspend fun updateExpense(expense: ExpenseEntity)

  @Query("DELETE FROM expenses WHERE id = :id AND mosqueId = :mosqueId")
  suspend fun deleteExpense(id: String, mosqueId: String)
}

@Dao
interface UtilityBillDao {
  @Query("SELECT * FROM utility_bills WHERE mosqueId = :mosqueId ORDER BY dueDate DESC")
  fun getUtilityBillsForMosque(mosqueId: String): Flow<List<UtilityBillEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUtilityBill(bill: UtilityBillEntity)

  @Update
  suspend fun updateUtilityBill(bill: UtilityBillEntity)

  @Delete
  suspend fun deleteUtilityBill(bill: UtilityBillEntity)
}

@Dao
interface SalaryDao {
  @Query("SELECT * FROM salaries WHERE mosqueId = :mosqueId ORDER BY paymentDate DESC")
  fun getSalariesForMosque(mosqueId: String): Flow<List<SalaryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSalary(salary: SalaryEntity)

  @Update
  suspend fun updateSalary(salary: SalaryEntity)

  @Delete
  suspend fun deleteSalary(salary: SalaryEntity)
}

@Dao
interface BudgetDao {
  @Query("SELECT * FROM budgets WHERE mosqueId = :mosqueId AND periodType = :periodType AND periodName = :periodName")
  fun getBudgetsForPeriod(mosqueId: String, periodType: BudgetPeriod, periodName: String): Flow<List<BudgetEntity>>

  @Query("SELECT * FROM budgets WHERE mosqueId = :mosqueId")
  fun getAllBudgets(mosqueId: String): Flow<List<BudgetEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBudget(budget: BudgetEntity)

  @Update
  suspend fun updateBudget(budget: BudgetEntity)

  @Delete
  suspend fun deleteBudget(budget: BudgetEntity)
}

@Dao
interface MeetingDao {
  @Query("SELECT * FROM meetings WHERE mosqueId = :mosqueId ORDER BY date DESC")
  fun getMeetingsForMosque(mosqueId: String): Flow<List<MeetingEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMeeting(meeting: MeetingEntity)

  @Update
  suspend fun updateMeeting(meeting: MeetingEntity)

  @Delete
  suspend fun deleteMeeting(meeting: MeetingEntity)
}

@Dao
interface SmsLogDao {
  @Query("SELECT * FROM sms_logs WHERE mosqueId = :mosqueId ORDER BY sentAt DESC")
  fun getSmsLogsForMosque(mosqueId: String): Flow<List<SmsLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSmsLog(smsLog: SmsLogEntity)
}

@Dao
interface AuditLogDao {
  @Query("SELECT * FROM audit_logs WHERE mosqueId = :mosqueId ORDER BY timestamp DESC")
  fun getAuditLogsForMosque(mosqueId: String): Flow<List<AuditLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAuditLog(log: AuditLogEntity)
}

@Dao
interface AppSettingsDao {
  @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
  fun observeSettings(): Flow<AppSettingsEntity?>

  @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
  suspend fun getSettings(): AppSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(settings: AppSettingsEntity)
}
