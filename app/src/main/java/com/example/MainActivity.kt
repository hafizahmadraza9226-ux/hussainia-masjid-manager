package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.MasjidViewModel
import com.example.ui.components.MosqueTopBar
import com.example.ui.screens.*
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.MasjidManagerTheme

class MainActivity : ComponentActivity() {
  private val viewModel: MasjidViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val context = LocalContext.current
      val settings by viewModel.settings.collectAsStateWithLifecycle()
      val isSystemDark = isSystemInDarkTheme()
      val isDarkMode = when {
        settings?.useSystemTheme == true -> isSystemDark
        settings?.isDarkMode == true -> true
        else -> false
      }

      MasjidManagerTheme(darkTheme = isDarkMode) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          val activeMosque by viewModel.activeMosque.collectAsStateWithLifecycle()
          val allMosques by viewModel.allMosques.collectAsStateWithLifecycle()
          val dashboardState by viewModel.dashboardState.collectAsStateWithLifecycle()
          val incomes by viewModel.incomes.collectAsStateWithLifecycle()
          val expenses by viewModel.expenses.collectAsStateWithLifecycle()
          val receipts by viewModel.receipts.collectAsStateWithLifecycle()
          val donors by viewModel.donors.collectAsStateWithLifecycle()
          val utilityBills by viewModel.utilityBills.collectAsStateWithLifecycle()
          val salaries by viewModel.salaries.collectAsStateWithLifecycle()
          val budgets by viewModel.budgets.collectAsStateWithLifecycle()
          val meetings by viewModel.meetings.collectAsStateWithLifecycle()
          val members by viewModel.members.collectAsStateWithLifecycle()
          val smsLogs by viewModel.smsLogs.collectAsStateWithLifecycle()

          var currentTab by remember { mutableIntStateOf(0) } // 0: ڈیش بورڈ, 1: آمدن, 2: رسیدیں, 3: اخراجات, 4: ترتیبات و رپورٹس
          var showSwitchMosqueSheet by remember { mutableStateOf(false) }
          var showCreateMosqueDialog by remember { mutableStateOf(false) }
          var showJoinMosqueDialog by remember { mutableStateOf(false) }

          BackHandler(enabled = currentTab != 0) {
            currentTab = 0
          }

          Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
              MosqueTopBar(
                activeMosque = activeMosque,
                activeRole = settings?.currentActiveUserRole ?: UserRole.SUPER_ADMIN,
                onSwitchMosqueClick = { showSwitchMosqueSheet = true }
              )
            },
            bottomBar = {
              NavigationBar(
                modifier = Modifier
                  .windowInsetsPadding(WindowInsets.navigationBars)
                  .testTag("main_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
              ) {
                NavigationBarItem(
                  selected = currentTab == 0,
                  onClick = { currentTab = 0 },
                  icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                  label = { Text("ڈیش بورڈ", fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal) },
                  modifier = Modifier.testTag("nav_item_dashboard")
                )
                NavigationBarItem(
                  selected = currentTab == 1,
                  onClick = { currentTab = 1 },
                  icon = { Icon(Icons.Default.TrendingUp, contentDescription = null) },
                  label = { Text("آمدن و چندہ", fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal) },
                  modifier = Modifier.testTag("nav_item_income")
                )
                NavigationBarItem(
                  selected = currentTab == 2,
                  onClick = { currentTab = 2 },
                  icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                  label = { Text("رسیدیں", fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal) },
                  modifier = Modifier.testTag("nav_item_receipts")
                )
                NavigationBarItem(
                  selected = currentTab == 3,
                  onClick = { currentTab = 3 },
                  icon = { Icon(Icons.Default.Payments, contentDescription = null) },
                  label = { Text("اخراجات و بل", fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Normal) },
                  modifier = Modifier.testTag("nav_item_expenses")
                )
                NavigationBarItem(
                  selected = currentTab == 4,
                  onClick = { currentTab = 4 },
                  icon = { Icon(Icons.Default.Tune, contentDescription = null) },
                  label = { Text("رپورٹس", fontWeight = if (currentTab == 4) FontWeight.Bold else FontWeight.Normal) },
                  modifier = Modifier.testTag("nav_item_reports")
                )
              }
            }
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              when (currentTab) {
                0 -> {
                  DashboardScreen(
                    mosque = activeMosque,
                    dashboardState = dashboardState,
                    recentIncomes = incomes,
                    recentExpenses = expenses,
                    onAddIncomeClick = { currentTab = 1 },
                    onAddExpenseClick = { currentTab = 3 },
                    onAddBillClick = { currentTab = 3 },
                    onViewReceiptsClick = { currentTab = 2 }
                  )
                }

                1 -> {
                  IncomeAndDonationsScreen(
                    incomes = incomes,
                    donors = donors,
                    onAddIncomeSubmit = { amt, cat, donor, phone, addr, purp, method, notes, sms ->
                      viewModel.recordIncome(
                        amount = amt,
                        category = cat,
                        donorName = donor,
                        donorPhone = phone,
                        donorAddress = addr,
                        purpose = purp,
                        paymentMethod = method,
                        notes = notes,
                        sendSmsImmediately = sms,
                        onSuccess = { r ->
                          Toast.makeText(context, "رسید نمبر ${r.receiptNumber} جاری ہو گئی!", Toast.LENGTH_SHORT).show()
                        }
                      )
                    },
                    onAddDonorSubmit = { name, phone, addr, amt, notes ->
                      activeMosque?.let { m ->
                        viewModel.saveDonor(
                          com.example.data.model.DonorEntity(
                            id = java.util.UUID.randomUUID().toString(),
                            mosqueId = m.id,
                            name = name,
                            phone = phone,
                            address = addr,
                            pledgedMonthlyAmount = amt,
                            notes = notes
                          )
                        )
                        Toast.makeText(context, "معاون $name کامیابی سے شامل ہو گیا!", Toast.LENGTH_SHORT).show()
                      }
                    },
                    onViewReceipt = { _ ->
                      currentTab = 2
                    }
                  )
                }

                2 -> {
                  ReceiptsScreen(
                    mosque = activeMosque,
                    receipts = receipts,
                    onCancelReceipt = { r ->
                      viewModel.cancelReceipt(r)
                      Toast.makeText(context, "رسید نمبر ${r.receiptNumber} منسوخ کر دی گئی۔", Toast.LENGTH_SHORT).show()
                    }
                  )
                }

                3 -> {
                  ExpensesAndUtilitiesScreen(
                    mosqueId = activeMosque?.id ?: "",
                    expenses = expenses,
                    utilityBills = utilityBills,
                    salaries = salaries,
                    onAddExpenseSubmit = { amt, cat, payee, ref, method, details, notes ->
                      viewModel.recordExpense(
                        amount = amt,
                        category = cat,
                        payeeName = payee,
                        billReceiptRef = ref,
                        paymentMethod = method,
                        details = details,
                        notes = notes
                      )
                      Toast.makeText(context, "خرچ کامیابی سے محفوظ ہو گیا!", Toast.LENGTH_SHORT).show()
                    },
                    onSaveUtilityBill = { bill ->
                      viewModel.saveUtilityBill(bill)
                      Toast.makeText(context, "یوٹیلیٹی بل محفوظ ہو گیا!", Toast.LENGTH_SHORT).show()
                    },
                    onUpdateUtilityBill = { bill ->
                      viewModel.updateUtilityBill(bill)
                      Toast.makeText(context, "بل کی ادائیگی ریکارڈ ہو گئی!", Toast.LENGTH_SHORT).show()
                    },
                    onSaveSalary = { sal ->
                      viewModel.saveSalary(sal)
                      Toast.makeText(context, "تنخواہ کی ادائیگی ریکارڈ ہو گئی!", Toast.LENGTH_SHORT).show()
                    }
                  )
                }

                4 -> {
                  ManagementAndReportsScreen(
                    mosque = activeMosque,
                    incomes = incomes,
                    expenses = expenses,
                    donors = donors,
                    budgets = budgets,
                    meetings = meetings,
                    members = members,
                    smsLogs = smsLogs,
                    isDarkMode = isDarkMode,
                    onSaveBudget = { b ->
                      viewModel.saveBudget(b)
                      Toast.makeText(context, "بجٹ محفوظ ہو گیا!", Toast.LENGTH_SHORT).show()
                    },
                    onSaveMeeting = { m ->
                      viewModel.saveMeeting(m)
                      Toast.makeText(context, "اجلاس کا ریکارڈ محفوظ ہو گیا!", Toast.LENGTH_SHORT).show()
                    },
                    onUpdateMosque = { m ->
                      viewModel.updateMosqueProfile(m)
                      Toast.makeText(context, "مسجد کی ترتیبات اپ ڈیٹ ہو گئیں!", Toast.LENGTH_SHORT).show()
                    },
                    onToggleTheme = { dark ->
                      viewModel.toggleTheme(isDark = dark, useSystem = false)
                    }
                  )
                }
              }
            }

            // Bottom Sheets & Dialogs
            if (showSwitchMosqueSheet) {
              SwitchMosqueBottomSheet(
                allMosques = allMosques,
                activeMosqueId = activeMosque?.id ?: "",
                onSelectMosque = { id ->
                  viewModel.switchActiveMosque(id)
                },
                onCreateNewClick = {
                  showCreateMosqueDialog = true
                },
                onJoinClick = {
                  showJoinMosqueDialog = true
                },
                onDismiss = { showSwitchMosqueSheet = false }
              )
            }

            if (showCreateMosqueDialog) {
              CreateMosqueDialog(
                onDismiss = { showCreateMosqueDialog = false },
                onSubmit = { name, shortName, code, addr, city, phone, email, adminName, adminEmail, adminPhone ->
                  viewModel.createNewMosque(
                    name = name,
                    shortName = shortName,
                    code = code,
                    address = addr,
                    city = city,
                    phone = phone,
                    email = email,
                    adminName = adminName,
                    adminEmail = adminEmail,
                    adminPhone = adminPhone,
                    onSuccess = { newId ->
                      showCreateMosqueDialog = false
                      Toast.makeText(context, "نئی مسجد '$name' کا الگ ورکسپیس کامیابی سے بن گیا!", Toast.LENGTH_LONG).show()
                    }
                  )
                }
              )
            }

            if (showJoinMosqueDialog) {
              JoinMosqueDialog(
                onDismiss = { showJoinMosqueDialog = false },
                onSubmit = { code, name, email, phone ->
                  viewModel.joinMosque(
                    inviteCode = code,
                    name = name,
                    email = email,
                    phone = phone,
                    onResult = { res ->
                      if (res.isSuccess) {
                        showJoinMosqueDialog = false
                        Toast.makeText(context, "آپ کامیابی سے '${res.getOrNull()}' کے ورکسپیس میں شامل ہو گئے!", Toast.LENGTH_LONG).show()
                      } else {
                        Toast.makeText(context, res.exceptionOrNull()?.message ?: "خرابی", Toast.LENGTH_LONG).show()
                      }
                    }
                  )
                }
              )
            }
          }
        }
      }
    }
  }
}
