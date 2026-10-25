package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeAndDonationsScreen(
  incomes: List<IncomeEntity>,
  donors: List<DonorEntity>,
  onAddIncomeSubmit: (
    amount: Double,
    category: IncomeCategory,
    donorName: String,
    donorPhone: String,
    donorAddress: String,
    purpose: String,
    paymentMethod: PaymentMethod,
    notes: String,
    sendSmsImmediately: Boolean
  ) -> Unit,
  onAddDonorSubmit: (name: String, phone: String, address: String, monthlyAmount: Double, notes: String) -> Unit,
  onViewReceipt: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: آمدن, 1: معاونین
  var showAddIncomeDialog by remember { mutableStateOf(false) }
  var showAddDonorDialog by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategoryFilter by remember { mutableStateOf<IncomeCategory?>(null) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = {
          if (selectedTab == 0) showAddIncomeDialog = true else showAddDonorDialog = true
        },
        containerColor = IslamicGreen,
        contentColor = Color.White,
        modifier = Modifier.testTag("add_income_or_donor_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (selectedTab == 0) "نئی آمدن درج کریں" else "نیا معاون شامل کریں")
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

      // Tab selector
      PrimaryTabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = IslamicGreen
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("آمدن ریکارڈ (${incomes.size})", fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.TrendingUp, contentDescription = null) },
          modifier = Modifier.testTag("tab_incomes")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("مستقل معاونین (${donors.size})", fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.People, contentDescription = null) },
          modifier = Modifier.testTag("tab_donors")
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Search bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text(if (selectedTab == 0) "نام، رسید نمبر یا موبائل سے تلاش کریں..." else "معاون کا نام یا فون...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("income_search_bar"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      if (selectedTab == 0) {
        val filteredIncomes = incomes.filter { inc ->
          val matchesSearch = searchQuery.isBlank() ||
            inc.donorName.contains(searchQuery, ignoreCase = true) ||
            inc.receiptNumber.contains(searchQuery, ignoreCase = true) ||
            inc.donorPhone.contains(searchQuery)
          val matchesCategory = selectedCategoryFilter == null || inc.category == selectedCategoryFilter
          matchesSearch && matchesCategory
        }

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            // Category Quick Chips
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              FilterChip(
                selected = selectedCategoryFilter == null,
                onClick = { selectedCategoryFilter = null },
                label = { Text("تمام") },
                shape = RoundedCornerShape(8.dp)
              )
              IncomeCategory.values().take(3).forEach { cat ->
                FilterChip(
                  selected = selectedCategoryFilter == cat,
                  onClick = { selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat },
                  label = { Text(cat.urduTitle) },
                  shape = RoundedCornerShape(8.dp)
                )
              }
            }
          }

          if (filteredIncomes.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(40.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "کوئی آمدن اندراج نہیں ملا",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          } else {
            items(filteredIncomes) { item ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onViewReceipt(item.receiptNumber) }
                  .testTag("income_card_${item.id}"),
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
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(IslamicGreenContainer),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Receipt, contentDescription = null, tint = IslamicGreen)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = item.donorName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        text = "${item.category.urduTitle} • ${item.paymentMethod.urduTitle}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = "${item.receiptNumber} • ${DateUtils.formatUrduDate(item.createdAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatteGoldDark
                      )
                    }
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    Text(
                      text = UrduNumberHelper.formatAmount(item.amount),
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = SuccessGreen
                    )
                    Text(
                      text = "رسید دیکھیں >",
                      style = MaterialTheme.typography.labelSmall,
                      color = IslamicGreen
                    )
                  }
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(70.dp)) }
        }
      } else {
        // Regular Donors List
        val filteredDonors = donors.filter { d ->
          searchQuery.isBlank() || d.name.contains(searchQuery, ignoreCase = true) || d.phone.contains(searchQuery)
        }

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (filteredDonors.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(40.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "کوئی مستقل معاون درج نہیں ہے",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          } else {
            items(filteredDonors) { donor ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("donor_card_${donor.id}"),
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
                        .background(MatteGoldContainer),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Person, contentDescription = null, tint = MatteGoldDark)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = donor.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        text = if (donor.phone.isNotBlank()) "فون: ${donor.phone}" else "کوئی فون درج نہیں",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      if (donor.address.isNotBlank()) {
                        Text(
                          text = donor.address,
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    Text(
                      text = "ماہانہ تعاون",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = UrduNumberHelper.formatAmount(donor.pledgedMonthlyAmount),
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = IslamicGreen
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

  // Add Income Dialog
  if (showAddIncomeDialog) {
    AddIncomeDialog(
      onDismiss = { showAddIncomeDialog = false },
      onSubmit = { amount, cat, name, phone, addr, purp, method, notes, sms ->
        onAddIncomeSubmit(amount, cat, name, phone, addr, purp, method, notes, sms)
        showAddIncomeDialog = false
      }
    )
  }

  // Add Donor Dialog
  if (showAddDonorDialog) {
    AddDonorDialog(
      onDismiss = { showAddDonorDialog = false },
      onSubmit = { name, phone, addr, amount, notes ->
        onAddDonorSubmit(name, phone, addr, amount, notes)
        showAddDonorDialog = false
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeDialog(
  onDismiss: () -> Unit,
  onSubmit: (
    amount: Double,
    category: IncomeCategory,
    donorName: String,
    donorPhone: String,
    donorAddress: String,
    purpose: String,
    paymentMethod: PaymentMethod,
    notes: String,
    sendSmsImmediately: Boolean
  ) -> Unit
) {
  var amountStr by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(IncomeCategory.GENERAL_DONATION) }
  var donorName by remember { mutableStateOf("") }
  var donorPhone by remember { mutableStateOf("") }
  var donorAddress by remember { mutableStateOf("") }
  var purpose by remember { mutableStateOf("") }
  var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
  var notes by remember { mutableStateOf("") }
  var sendSmsImmediately by remember { mutableStateOf(true) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val amount = amountStr.toDoubleOrNull() ?: 0.0

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)
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
              text = "نئی آمدن و رسید کا اجراء",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = IslamicGreen
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
            label = { Text("رقم (روپے) *") },
            placeholder = { Text("مثلاً: 5000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("income_amount_input"),
            shape = RoundedCornerShape(12.dp)
          )
        }

        if (amount > 0) {
          item {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MatteGoldContainer.copy(alpha = 0.5f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "الفاظ میں: " + UrduNumberHelper.convertToUrduWords(amount),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = OnMatteGoldContainer,
                modifier = Modifier.padding(8.dp)
              )
            }
          }
        }

        item {
          Text("آمدن کی قسم:", style = MaterialTheme.typography.labelMedium)
          Spacer(modifier = Modifier.height(4.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IncomeCategory.values().take(3).forEach { cat ->
              FilterChip(
                selected = selectedCategory == cat,
                onClick = { selectedCategory = cat },
                label = { Text(cat.urduTitle, style = MaterialTheme.typography.labelSmall) }
              )
            }
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IncomeCategory.values().drop(3).take(3).forEach { cat ->
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
            value = donorName,
            onValueChange = { donorName = it },
            label = { Text("معاون / چندہ دہندہ کا نام *") },
            placeholder = { Text("مثلاً: چوہدری نوید احمد") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("income_donor_name_input"),
            shape = RoundedCornerShape(12.dp)
          )
        }

        item {
          OutlinedTextField(
            value = donorPhone,
            onValueChange = { donorPhone = it },
            label = { Text("موبائل نمبر (برائے تصدیقی SMS)") },
            placeholder = { Text("03001234567") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }

        item {
          OutlinedTextField(
            value = donorAddress,
            onValueChange = { donorAddress = it },
            label = { Text("پتہ / محلہ") },
            placeholder = { Text("اختیاری") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }

        item {
          Text("ادائیگی کا طریقہ:", style = MaterialTheme.typography.labelMedium)
          Spacer(modifier = Modifier.height(4.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PaymentMethod.values().take(3).forEach { pm ->
              FilterChip(
                selected = selectedPaymentMethod == pm,
                onClick = { selectedPaymentMethod = pm },
                label = { Text(pm.urduTitle, style = MaterialTheme.typography.labelSmall) }
              )
            }
          }
        }

        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Checkbox(
              checked = sendSmsImmediately,
              onCheckedChange = { sendSmsImmediately = it }
            )
            Text(
              text = "تصدیقی SMS لاگ بنائیں اور ارسال کریں",
              style = MaterialTheme.typography.bodySmall
            )
          }
        }

        errorMessage?.let {
          item {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
          }
        }

        item {
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              if (amount <= 0) {
                errorMessage = "درست رقم درج کریں۔"
                return@Button
              }
              if (donorName.isBlank()) {
                errorMessage = "چندہ دینے والے کا نام درج کرنا لازمی ہے۔"
                return@Button
              }
              onSubmit(
                amount, selectedCategory, donorName, donorPhone, donorAddress,
                purpose, selectedPaymentMethod, notes, sendSmsImmediately
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("submit_income_button")
          ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("رسید جاری کریں و محفوظ کریں", style = MaterialTheme.typography.labelLarge)
          }
        }
      }
    }
  }
}

@Composable
fun AddDonorDialog(
  onDismiss: () -> Unit,
  onSubmit: (name: String, phone: String, address: String, amount: Double, notes: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var amountStr by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }
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
            text = "نیا مستقل معاون",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = IslamicGreen
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("معاون کا نام *") },
          placeholder = { Text("مثلاً: حاجی منظور احمد") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("donor_name_input"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("موبائل نمبر") },
          placeholder = { Text("03xx-xxxxxxx") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = amountStr,
          onValueChange = { amountStr = it },
          label = { Text("طے شدہ ماہانہ تعاون (روپے) *") },
          placeholder = { Text("مثلاً: 2000") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("پتہ") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        errorMessage?.let {
          Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
          onClick = {
            val amt = amountStr.toDoubleOrNull() ?: 0.0
            if (name.isBlank() || amt <= 0) {
              errorMessage = "معاون کا نام اور درست ماہانہ رقم درج کریں۔"
              return@Button
            }
            onSubmit(name, phone, address, amt, notes)
          },
          colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("submit_donor_button")
        ) {
          Icon(Icons.Default.Done, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("معاون محفوظ کریں", style = MaterialTheme.typography.labelLarge)
        }
      }
    }
  }
}
