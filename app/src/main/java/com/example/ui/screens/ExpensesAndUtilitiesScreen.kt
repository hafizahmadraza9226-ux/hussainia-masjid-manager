package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.util.DateUtils
import com.example.util.UrduNumberHelper
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesAndUtilitiesScreen(
  mosqueId: String,
  expenses: List<ExpenseEntity>,
  utilityBills: List<UtilityBillEntity>,
  salaries: List<SalaryEntity>,
  onAddExpenseSubmit: (
    amount: Double,
    category: ExpenseCategory,
    payeeName: String,
    billRef: String,
    method: PaymentMethod,
    details: String,
    notes: String
  ) -> Unit,
  onSaveUtilityBill: (UtilityBillEntity) -> Unit,
  onUpdateUtilityBill: (UtilityBillEntity) -> Unit,
  onSaveSalary: (SalaryEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: اخراجات, 1: یوٹیلیٹی بل, 2: تنخواہیں
  var showAddExpenseDialog by remember { mutableStateOf(false) }
  var showAddBillDialog by remember { mutableStateOf(false) }
  var showAddSalaryDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = {
          when (selectedTab) {
            0 -> showAddExpenseDialog = true
            1 -> showAddBillDialog = true
            2 -> showAddSalaryDialog = true
          }
        },
        containerColor = when (selectedTab) {
          0 -> MatteGoldDark
          1 -> InfoBlue
          else -> IslamicGreen
        },
        contentColor = Color.White,
        modifier = Modifier.testTag("add_expense_or_utility_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          when (selectedTab) {
            0 -> "نیا خرچ درج کریں"
            1 -> "نیا یوٹیلیٹی بل"
            else -> "تنخواہ کی ادائیگی"
          }
        )
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(8.dp))

      PrimaryTabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MatteGoldDark
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("اخراجات (${expenses.size})", fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.RemoveCircleOutline, contentDescription = null) },
          modifier = Modifier.testTag("tab_expenses")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("یوٹیلیٹی بلز (${utilityBills.size})", fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.Bolt, contentDescription = null) },
          modifier = Modifier.testTag("tab_utilities")
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("تنخواہیں (${salaries.size})", fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.Badge, contentDescription = null) },
          modifier = Modifier.testTag("tab_salaries")
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      when (selectedTab) {
        0 -> {
          // Expenses List
          val totalExpenses = expenses.sumOf { it.amount }
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("کل منظور شدہ اخراجات:", style = MaterialTheme.typography.bodyMedium)
              Text(
                text = UrduNumberHelper.formatAmount(totalExpenses),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ErrorRed
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          if (expenses.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
              contentAlignment = Alignment.Center
            ) {
              Text("کوئی اخراجات درج نہیں ہیں۔", style = MaterialTheme.typography.bodyMedium)
            }
          } else {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(expenses) { exp ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_card_${exp.id}"),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                      Box(
                        modifier = Modifier
                          .size(40.dp)
                          .clip(CircleShape)
                          .background(Color(0xFFFFEBEE)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(
                          text = exp.category.urduTitle,
                          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                          color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                          text = "وصول کنندہ: ${exp.payeeName.ifBlank { "نامعلوم" }}",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                          text = "${exp.paymentMethod.urduTitle} • ${DateUtils.formatUrduDate(exp.date)}",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }

                    Text(
                      text = UrduNumberHelper.formatAmount(exp.amount),
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = ErrorRed
                    )
                  }
                }
              }
              item { Spacer(modifier = Modifier.height(70.dp)) }
            }
          }
        }

        1 -> {
          // Utility Bills List
          if (utilityBills.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
              contentAlignment = Alignment.Center
            ) {
              Text("کوئی یوٹیلیٹی بل درج نہیں ہے۔", style = MaterialTheme.typography.bodyMedium)
            }
          } else {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(utilityBills) { bill ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("utility_bill_card_${bill.id}"),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                          shape = CircleShape,
                          color = when (bill.status) {
                            BillStatus.PAID -> IslamicGreenContainer
                            BillStatus.OVERDUE -> Color(0xFFFFCDD2)
                            BillStatus.UNPAID -> MatteGoldContainer
                          }
                        ) {
                          Text(
                            text = bill.status.urduTitle,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (bill.status) {
                              BillStatus.PAID -> IslamicGreen
                              BillStatus.OVERDUE -> ErrorRed
                              BillStatus.UNPAID -> OnMatteGoldContainer
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                          )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = "${bill.companyName} (${bill.utilityType.urduTitle})",
                          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                      }

                      Text(
                        text = UrduNumberHelper.formatAmount(bill.amount),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (bill.status == BillStatus.PAID) SuccessGreen else ErrorRed
                      )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = "ریفرنس: ${bill.consumerNumber} • ماہ: ${bill.billingMonth}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = "آخری تاریخ: ${DateUtils.formatUrduDate(bill.dueDate)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }

                    if (bill.status != BillStatus.PAID) {
                      Spacer(modifier = Modifier.height(8.dp))
                      Button(
                        onClick = {
                          onUpdateUtilityBill(
                            bill.copy(
                              status = BillStatus.PAID,
                              paidAmount = bill.amount,
                              paidDate = System.currentTimeMillis()
                            )
                          )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.End)
                      ) {
                        Text("ادا شدہ نشان لگائیں (Mark as Paid)", style = MaterialTheme.typography.labelSmall)
                      }
                    }
                  }
                }
              }
              item { Spacer(modifier = Modifier.height(70.dp)) }
            }
          }
        }

        2 -> {
          // Salaries
          if (salaries.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
              contentAlignment = Alignment.Center
            ) {
              Text("کوئی تنخواہ ریکارڈ درج نہیں ہے۔", style = MaterialTheme.typography.bodyMedium)
            }
          } else {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(salaries) { sal ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("salary_card_${sal.id}"),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(42.dp)
                          .clip(CircleShape)
                          .background(IslamicGreenContainer),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = IslamicGreen)
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(
                          text = sal.staffName,
                          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                          text = "${sal.staffType.urduTitle} • ماہ: ${sal.monthYear}",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                          text = "${sal.paymentMethod.urduTitle} • ${DateUtils.formatUrduDate(sal.paymentDate)}",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text(
                        text = UrduNumberHelper.formatAmount(sal.paidAmount),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SuccessGreen
                      )
                      if (sal.remainingAmount > 0) {
                        Text(
                          text = "بقایا: " + UrduNumberHelper.formatAmount(sal.remainingAmount),
                          style = MaterialTheme.typography.labelSmall,
                          color = ErrorRed
                        )
                      }
                    }
                  }
                }
              }
              item { Spacer(modifier = Modifier.height(70.dp)) }
            }
          }
        }
      }
    }
  }

  // Add Expense Dialog
  if (showAddExpenseDialog) {
    AddExpenseDialog(
      onDismiss = { showAddExpenseDialog = false },
      onSubmit = { amt, cat, payee, ref, method, details, notes ->
        onAddExpenseSubmit(amt, cat, payee, ref, method, details, notes)
        showAddExpenseDialog = false
      }
    )
  }

  // Add Utility Bill Dialog
  if (showAddBillDialog) {
    AddUtilityBillDialog(
      mosqueId = mosqueId,
      onDismiss = { showAddBillDialog = false },
      onSubmit = { bill ->
        onSaveUtilityBill(bill)
        showAddBillDialog = false
      }
    )
  }

  // Add Salary Dialog
  if (showAddSalaryDialog) {
    AddSalaryDialog(
      mosqueId = mosqueId,
      onDismiss = { showAddSalaryDialog = false },
      onSubmit = { sal ->
        onSaveSalary(sal)
        showAddSalaryDialog = false
      }
    )
  }
}

@Composable
fun AddExpenseDialog(
  onDismiss: () -> Unit,
  onSubmit: (
    amount: Double,
    category: ExpenseCategory,
    payeeName: String,
    billRef: String,
    method: PaymentMethod,
    details: String,
    notes: String
  ) -> Unit
) {
  var amountStr by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(ExpenseCategory.CLEANING) }
  var payeeName by remember { mutableStateOf("") }
  var billRef by remember { mutableStateOf("") }
  var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
  var details by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "مسجد کا نیا خرچ درج کریں",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = ErrorRed
            )
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }
        }

        item {
          OutlinedTextField(
            value = amountStr,
            onValueChange = { amountStr = it },
            label = { Text("خرچ کی رقم (روپے) *") },
            placeholder = { Text("مثلاً: 3500") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("expense_amount_input"),
            shape = RoundedCornerShape(12.dp)
          )
        }

        item {
          Text("خرچ کی مد / کیٹیگری:", style = MaterialTheme.typography.labelMedium)
          Spacer(modifier = Modifier.height(4.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(ExpenseCategory.CLEANING, ExpenseCategory.ELECTRICITY, ExpenseCategory.REPAIRS).forEach { cat ->
              FilterChip(
                selected = selectedCategory == cat,
                onClick = { selectedCategory = cat },
                label = { Text(cat.urduTitle, style = MaterialTheme.typography.labelSmall) }
              )
            }
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(ExpenseCategory.ELECTRICAL, ExpenseCategory.SOLAR_SYSTEM, ExpenseCategory.CARPETS_MATS).forEach { cat ->
              FilterChip(
                selected = selectedCategory == cat,
                onClick = { selectedCategory = cat },
                label = { Text(cat.urduTitle, style = MaterialTheme.typography.labelSmall) }
              )
            }
          }
        }

        item {
          OutlinedTextField(
            value = payeeName,
            onValueChange = { payeeName = it },
            label = { Text("دکاندار / وصول کنندہ کا نام *") },
            placeholder = { Text("مثلاً: اکرم الیکٹرونکس") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("expense_payee_input"),
            shape = RoundedCornerShape(12.dp)
          )
        }

        item {
          OutlinedTextField(
            value = billRef,
            onValueChange = { billRef = it },
            label = { Text("بل یا واؤچر نمبر") },
            placeholder = { Text("اختیاری") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }

        item {
          OutlinedTextField(
            value = details,
            onValueChange = { details = it },
            label = { Text("خرچ کی تفصیل") },
            placeholder = { Text("مثلاً: پنکھے کی مرمت، نئی وائرنگ") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }

        errorMessage?.let {
          item {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
          }
        }

        item {
          Spacer(modifier = Modifier.height(6.dp))
          Button(
            onClick = {
              val amt = amountStr.toDoubleOrNull() ?: 0.0
              if (amt <= 0 || payeeName.isBlank()) {
                errorMessage = "درست رقم اور وصول کنندہ کا نام درج کریں۔"
                return@Button
              }
              onSubmit(amt, selectedCategory, payeeName, billRef, selectedPaymentMethod, details, "")
            },
            colors = ButtonDefaults.buttonColors(containerColor = MatteGoldDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("submit_expense_button")
          ) {
            Icon(Icons.Default.Done, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("خرچ محفوظ کریں", style = MaterialTheme.typography.labelLarge)
          }
        }
      }
    }
  }
}

@Composable
fun AddUtilityBillDialog(
  mosqueId: String,
  onDismiss: () -> Unit,
  onSubmit: (UtilityBillEntity) -> Unit
) {
  var utilityType by remember { mutableStateOf(ExpenseCategory.ELECTRICITY) }
  var companyName by remember { mutableStateOf("LESCO") }
  var consumerNumber by remember { mutableStateOf("") }
  var billingMonth by remember { mutableStateOf(DateUtils.getCurrentMonthUrdu()) }
  var amountStr by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "نیا یوٹیلیٹی بل درج کریں",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = InfoBlue
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf(
            Triple(ExpenseCategory.ELECTRICITY, "بجلی (LESCO)", "LESCO"),
            Triple(ExpenseCategory.GAS, "گیس (SNGPL)", "SNGPL"),
            Triple(ExpenseCategory.WATER, "پانی (WASA)", "WASA")
          ).forEach { (cat, label, comp) ->
            FilterChip(
              selected = utilityType == cat,
              onClick = {
                utilityType = cat
                companyName = comp
              },
              label = { Text(label, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }

        OutlinedTextField(
          value = consumerNumber,
          onValueChange = { consumerNumber = it },
          label = { Text("ریفرنس یا کنزیومر نمبر *") },
          placeholder = { Text("مثلاً: 24 11223 3445500") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("bill_consumer_no_input"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = amountStr,
          onValueChange = { amountStr = it },
          label = { Text("بل کی رقم (روپے) *") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("bill_amount_input"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = billingMonth,
          onValueChange = { billingMonth = it },
          label = { Text("بل کا مہینہ") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        errorMessage?.let {
          Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
          onClick = {
            val amt = amountStr.toDoubleOrNull() ?: 0.0
            if (amt <= 0 || consumerNumber.isBlank()) {
              errorMessage = "درست رقم اور ریفرنس نمبر درج کریں۔"
              return@Button
            }
            onSubmit(
              UtilityBillEntity(
                id = UUID.randomUUID().toString(),
                mosqueId = mosqueId,
                utilityType = utilityType,
                companyName = companyName,
                consumerNumber = consumerNumber.trim(),
                billingMonth = billingMonth,
                issueDate = System.currentTimeMillis(),
                dueDate = System.currentTimeMillis() + 86400000L * 7,
                amount = amt,
                status = BillStatus.UNPAID
              )
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = InfoBlue),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("submit_bill_button")
        ) {
          Icon(Icons.Default.Done, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("بل محفوظ کریں", style = MaterialTheme.typography.labelLarge)
        }
      }
    }
  }
}

@Composable
fun AddSalaryDialog(
  mosqueId: String,
  onDismiss: () -> Unit,
  onSubmit: (SalaryEntity) -> Unit
) {
  var staffType by remember { mutableStateOf(StaffType.IMAM) }
  var staffName by remember { mutableStateOf("مولانا حافظ احمد رضا") }
  var salaryAmountStr by remember { mutableStateOf("45000") }
  var monthYear by remember { mutableStateOf(DateUtils.getCurrentMonthUrdu()) }
  var selectedMethod by remember { mutableStateOf(PaymentMethod.CASH) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "تنخواہ کی ادائیگی کا اندراج",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = IslamicGreen
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          StaffType.values().forEach { st ->
            FilterChip(
              selected = staffType == st,
              onClick = {
                staffType = st
                staffName = when (st) {
                  StaffType.IMAM -> "مولانا حافظ احمد رضا صاحب"
                  StaffType.MOAZZIN -> "قاری محمد بلال صاحب"
                  StaffType.KHADIM -> "خادم صاحب"
                }
                salaryAmountStr = when (st) {
                  StaffType.IMAM -> "45000"
                  StaffType.MOAZZIN -> "28000"
                  StaffType.KHADIM -> "20000"
                }
              },
              label = { Text(st.urduTitle, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }

        OutlinedTextField(
          value = staffName,
          onValueChange = { staffName = it },
          label = { Text("نام ملازم *") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("salary_staff_name_input"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = salaryAmountStr,
          onValueChange = { salaryAmountStr = it },
          label = { Text("ادا شدہ رقم (روپے) *") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("salary_amount_input"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = monthYear,
          onValueChange = { monthYear = it },
          label = { Text("متعلقہ مہینہ") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        errorMessage?.let {
          Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
          onClick = {
            val amt = salaryAmountStr.toDoubleOrNull() ?: 0.0
            if (amt <= 0 || staffName.isBlank()) {
              errorMessage = "درست نام اور رقم درج کریں۔"
              return@Button
            }
            onSubmit(
              SalaryEntity(
                id = UUID.randomUUID().toString(),
                mosqueId = mosqueId,
                staffType = staffType,
                staffName = staffName.trim(),
                monthlySalary = amt,
                monthYear = monthYear,
                paidAmount = amt,
                remainingAmount = 0.0,
                paymentDate = System.currentTimeMillis(),
                paymentMethod = selectedMethod,
                notes = "بروقت ادائیگی"
              )
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("submit_salary_button")
        ) {
          Icon(Icons.Default.Done, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("تنخواہ محفوظ کریں", style = MaterialTheme.typography.labelLarge)
        }
      }
    }
  }
}
