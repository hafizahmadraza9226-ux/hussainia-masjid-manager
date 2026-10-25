package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.*

@Database(
  entities = [
    MosqueEntity::class,
    MosqueMemberEntity::class,
    IncomeEntity::class,
    DonorEntity::class,
    ReceiptEntity::class,
    ExpenseEntity::class,
    UtilityBillEntity::class,
    SalaryEntity::class,
    BudgetEntity::class,
    MeetingEntity::class,
    SmsLogEntity::class,
    AuditLogEntity::class,
    AppSettingsEntity::class
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun mosqueDao(): MosqueDao
  abstract fun mosqueMemberDao(): MosqueMemberDao
  abstract fun incomeDao(): IncomeDao
  abstract fun donorDao(): DonorDao
  abstract fun receiptDao(): ReceiptDao
  abstract fun expenseDao(): ExpenseDao
  abstract fun utilityBillDao(): UtilityBillDao
  abstract fun salaryDao(): SalaryDao
  abstract fun budgetDao(): BudgetDao
  abstract fun meetingDao(): MeetingDao
  abstract fun smsLogDao(): SmsLogDao
  abstract fun auditLogDao(): AuditLogDao
  abstract fun appSettingsDao(): AppSettingsDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "masjid_manager_db"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
