package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
fun ManagementAndReportsScreen(
  mosque: MosqueEntity?,
  incomes: List<IncomeEntity>,
  expenses: List<ExpenseEntity>,
  donors: List<DonorEntity>,
  budgets: List<BudgetEntity>,
  meetings: List<MeetingEntity>,
  members: List<MosqueMemberEntity>,
  smsLogs: List<SmsLogEntity>,
  isDarkMode: Boolean,
  onSaveBudget: (BudgetEntity) -> Unit,
  onSaveMeeting: (MeetingEntity) -> Unit,
  onUpdateMosque: (MosqueEntity) -> Unit,
  onToggleTheme: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(0) } // 0: رپورٹس, 1: بجٹ, 2: اجلاس, 3: سیٹنگز و صارفین

  var showAddBudgetDialog by remember { mutableStateOf(false) }
  var showAddMeetingDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    PrimaryScrollableTabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = IslamicGreen,
      edgePadding = 0.dp
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("مالی رپورٹس", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
        modifier = Modifier.testTag("tab_reports")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("بجٹ موازنہ", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Default.PieChart, contentDescription = null) },
        modifier = Modifier.testTag("tab_budgets")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("اجلاس و فیصلے", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Default.Groups, contentDescription = null) },
        modifier = Modifier.testTag("tab_meetings")
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { selectedTab = 3 },
        text = { Text("سیٹنگز و 4 صارفین", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
        modifier = Modifier.testTag("tab_settings")
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    when (selectedTab) {
      0 -> {
        // Financial Reports & Analytics Tab
        val totalInc = incomes.filter { it.status == TransactionStatus.CONFIRMED }.sumOf { it.amount }
        val totalExp = expenses.filter { it.status == TransactionStatus.CONFIRMED }.sumOf { it.amount }
        val netSurplus = totalInc - totalExp

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "مجموعی مالی گوشوارہ برائے ${mosque?.name ?: "مسجد"}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = IslamicGreen
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("کل موصول شدہ آمدن:", style = MaterialTheme.typography.bodyMedium)
                  Text(UrduNumberHelper.formatAmount(totalInc), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = SuccessGreen)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("کل ادا شدہ اخراجات:", style = MaterialTheme.typography.bodyMedium)
                  Text(UrduNumberHelper.formatAmount(totalExp), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = ErrorRed)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("خالص بچت / سرپلس:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                  Text(
                    text = UrduNumberHelper.formatAmount(netSurplus),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (netSurplus >= 0) IslamicGreen else ErrorRed
                  )
                }
              }
            }
          }

          // CSV / Excel Export Action
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MatteGoldContainer)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "رپورٹ ایکسپورٹ (CSV / ٹیکسٹ فارمیٹ)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = OnMatteGoldContainer
                  )
                  Text(
                    text = "تمام آمدن و اخراجات کی تفصیلات شیئر یا کاپی کریں۔",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnMatteGoldContainer.copy(alpha = 0.8f)
                  )
                }

                Button(
                  onClick = {
                    val csvReport = buildString {
                      appendLine("مسجد مینجمنٹ مالی رپورٹ")
                      appendLine("مسجد: ${mosque?.name}")
                      appendLine("تاریخ: ${DateUtils.getCurrentMonthUrdu()}")
                      appendLine("━━━━━━━━━━━━━━━━━━━")
                      appendLine("کل آمدن: ${UrduNumberHelper.formatAmount(totalInc)}")
                      appendLine("کل اخراجات: ${UrduNumberHelper.formatAmount(totalExp)}")
                      appendLine("خالص بیلنس: ${UrduNumberHelper.formatAmount(netSurplus)}")
                      appendLine("━━━━━━━━━━━━━━━━━━━")
                      appendLine("آمدن کی فہرست:")
                      incomes.forEach {
                        appendLine("${it.receiptNumber}, ${it.donorName}, ${it.category.urduTitle}, ${it.amount}, ${DateUtils.formatUrduDate(it.createdAt)}")
                      }
                      appendLine("━━━━━━━━━━━━━━━━━━━")
                      appendLine("اخراجات کی فہرست:")
                      expenses.forEach {
                        appendLine("${it.category.urduTitle}, ${it.payeeName}, ${it.amount}, ${DateUtils.formatUrduDate(it.date)}")
                      }
                    }

                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Masjid Report", csvReport))
                    Toast.makeText(context, "رپورٹ کلپ بورڈ پر کاپی ہو گئی!", Toast.LENGTH_SHORT).show()

                    val sendIntent = Intent().apply {
                      action = Intent.ACTION_SEND
                      putExtra(Intent.EXTRA_TEXT, csvReport)
                      type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "مالی رپورٹ شیئر کریں"))
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("ایکسپورٹ", style = MaterialTheme.typography.labelSmall)
                }
              }
            }
          }

          // Category Breakdown
          item {
            Text("آمدن کی مدات کا جائزہ:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
          }

          items(IncomeCategory.values().toList()) { cat ->
            val catTotal = incomes.filter { it.category == cat && it.status == TransactionStatus.CONFIRMED }.sumOf { it.amount }
            if (catTotal > 0) {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(cat.urduTitle, style = MaterialTheme.typography.bodyMedium)
                  Text(UrduNumberHelper.formatAmount(catTotal), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = IslamicGreen)
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(20.dp)) }
        }
      }

      1 -> {
        // Budget Management Tab
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "ماہانہ بجٹ بمقابلہ اصل اخراجات",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = IslamicGreen
              )

              Button(
                onClick = { showAddBudgetDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MatteGoldDark),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("set_budget_button")
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("بجٹ مقرر کریں", style = MaterialTheme.typography.labelSmall)
              }
            }
          }

          if (budgets.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(40.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "ابھی کوئی بجٹ مقرر نہیں کیا گیا۔ بجٹ مقرر کریں بٹن دبائیں۔",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          } else {
            items(budgets) { b ->
              val actualSpent = expenses.filter { it.category == b.category && it.status == TransactionStatus.CONFIRMED }.sumOf { it.amount }
              val progress = if (b.budgetedAmount > 0) (actualSpent / b.budgetedAmount).coerceIn(0.0, 1.0).toFloat() else 0f
              val isOverBudget = actualSpent > b.budgetedAmount

              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = b.category.urduTitle,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = "${UrduNumberHelper.formatAmount(actualSpent)} / ${UrduNumberHelper.formatAmount(b.budgetedAmount)}",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = if (isOverBudget) ErrorRed else IslamicGreen
                    )
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(8.dp)
                      .clip(CircleShape),
                    color = if (isOverBudget) ErrorRed else IslamicGreen,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                  )

                  Spacer(modifier = Modifier.height(6.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = if (isOverBudget) "بجٹ سے زیادہ خرچ ہو چکا ہے!" else "باقی گنجائش: ${UrduNumberHelper.formatAmount(b.budgetedAmount - actualSpent)}",
                      style = MaterialTheme.typography.labelSmall,
                      color = if (isOverBudget) ErrorRed else SuccessGreen
                    )
                    Text(
                      text = b.periodName,
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(20.dp)) }
        }
      }

      2 -> {
        // Meetings & Decisions Tab
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "انتظامی کمیٹی کے اجلاس و فیصلے",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = IslamicGreen
              )

              Button(
                onClick = { showAddMeetingDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_meeting_button")
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("نیا اجلاس درج کریں", style = MaterialTheme.typography.labelSmall)
              }
            }
          }

          if (meetings.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(40.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "ابھی کوئی اجلاس درج نہیں ہے۔",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          } else {
            items(meetings) { m ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = m.meetingNumber,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = IslamicGreen
                    )
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = IslamicGreenContainer
                    ) {
                      Text(
                        text = m.status.urduTitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = OnIslamicGreenContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "تاریخ: ${DateUtils.formatUrduDate(m.date)} • مقام: ${m.venue}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "ایجنڈا: ${m.agenda}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                  )
                  Text(
                    text = "فیصلے: ${m.decisions}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  if (m.assignedPerson.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "ذمہ دار شخص: ${m.assignedPerson}",
                      style = MaterialTheme.typography.labelSmall,
                      color = MatteGoldDark
                    )
                  }
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(20.dp)) }
        }
      }

      3 -> {
        // Settings & 4 Users Tab
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Theme Switch Card
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null, tint = MatteGold)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("ڈارک تھیم (Dark Mode)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(if (isDarkMode) "ڈارک موڈ فعال ہے" else "لائٹ موڈ فعال ہے", style = MaterialTheme.typography.labelSmall)
                  }
                }
                Switch(
                  checked = isDarkMode,
                  onCheckedChange = { onToggleTheme(it) },
                  modifier = Modifier.testTag("theme_switch")
                )
              }
            }
          }

          // Mosque Profile Card
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "مسجد کی معلومات و شناخت",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = IslamicGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("مسجد کا نام: ${mosque?.name}", style = MaterialTheme.typography.bodySmall)
                Text("رسید کوڈ: ${mosque?.code} • شہر: ${mosque?.city}", style = MaterialTheme.typography.bodySmall)
                Text("پتہ: ${mosque?.address}", style = MaterialTheme.typography.bodySmall)
                Text("رابطہ نمبر: ${mosque?.phone}", style = MaterialTheme.typography.bodySmall)
                Text("رسید پر اختیاری دعا: ${mosque?.receiptNote}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }

          // 4-User Roles & Invitation Codes
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "مسجد کے 4 مجاز صارفین اور دعوتی کوڈز",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = IslamicGreen
                  )
                  Surface(shape = CircleShape, color = IslamicGreenContainer) {
                    Text(
                      text = "${members.count { it.isConfirmed }}/4 فعال",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = OnIslamicGreenContainer,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "ہر مسجد کے لیے زیادہ سے زیادہ 4 صارفین کے محفوظ رولز ہیں۔ ساتھی اراکین کو جوڑنے کے لیے نیچے دیا گیا دعوتی کوڈ کاپی کر کے بھیجیں:",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                members.forEach { mem ->
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp)
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = mem.role.urduTitle,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MatteGoldDark
                          )
                          Spacer(modifier = Modifier.width(6.dp))
                          if (mem.isConfirmed) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = SuccessGreen, modifier = Modifier.size(14.dp))
                          }
                        }
                        Text(
                          text = mem.userName,
                          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                      }

                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = IslamicGreenContainer
                        ) {
                          Text(
                            text = mem.inviteCode,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = OnIslamicGreenContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                          )
                        }
                        IconButton(
                          onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Invite Code", mem.inviteCode))
                            Toast.makeText(context, "دعوتی کوڈ ${mem.inviteCode} کاپی ہو گیا!", Toast.LENGTH_SHORT).show()
                          }
                        ) {
                          Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code", modifier = Modifier.size(18.dp))
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          // SMS Gateway Settings & Log summary
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "SMS Gateway اور تصدیقی پیغامات",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = IslamicGreen
                )
                Text(
                  text = "رسید کے بعد چندہ دہندہ کو خودکار شکریہ کا SMS جاتا ہے۔ حقیقی SMS ارسال کرنے کے لیے اپنی SMS API Key درج کریں۔",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Sender ID: ${mosque?.smsSenderName}", style = MaterialTheme.typography.bodySmall)
                Text("SMS لاگز کی تعداد: ${smsLogs.size}", style = MaterialTheme.typography.bodySmall)
              }
            }
          }

          item { Spacer(modifier = Modifier.height(20.dp)) }
        }
      }
    }
  }

  // Set Budget Dialog
  if (showAddBudgetDialog && mosque != null) {
    SetBudgetDialog(
      mosqueId = mosque.id,
      onDismiss = { showAddBudgetDialog = false },
      onSubmit = { b ->
        onSaveBudget(b)
        showAddBudgetDialog = false
      }
    )
  }

  // Add Meeting Dialog
  if (showAddMeetingDialog && mosque != null) {
    AddMeetingDialog(
      mosqueId = mosque.id,
      onDismiss = { showAddMeetingDialog = false },
      onSubmit = { m ->
        onSaveMeeting(m)
        showAddMeetingDialog = false
      }
    )
  }
}

@Composable
fun SetBudgetDialog(
  mosqueId: String,
  onDismiss: () -> Unit,
  onSubmit: (BudgetEntity) -> Unit
) {
  var selectedCategory by remember { mutableStateOf(ExpenseCategory.ELECTRICITY) }
  var amountStr by remember { mutableStateOf("") }
  var periodName by remember { mutableStateOf(DateUtils.getCurrentMonthUrdu()) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
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
        Text("ماہانہ بجٹ کا تعین کریں", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = IslamicGreen)

        Text("مد / کیٹیگری:", style = MaterialTheme.typography.labelMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          listOf(ExpenseCategory.ELECTRICITY, ExpenseCategory.CLEANING, ExpenseCategory.REPAIRS).forEach { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text(cat.urduTitle, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }

        OutlinedTextField(
          value = amountStr,
          onValueChange = { amountStr = it },
          label = { Text("بجٹ رقم (روپے) *") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        Button(
          onClick = {
            val amt = amountStr.toDoubleOrNull() ?: 0.0
            if (amt > 0) {
              onSubmit(
                BudgetEntity(
                  id = UUID.randomUUID().toString(),
                  mosqueId = mosqueId,
                  periodType = BudgetPeriod.MONTHLY,
                  periodName = periodName,
                  category = selectedCategory,
                  budgetedAmount = amt
                )
              )
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("بجٹ محفوظ کریں")
        }
      }
    }
  }
}

@Composable
fun AddMeetingDialog(
  mosqueId: String,
  onDismiss: () -> Unit,
  onSubmit: (MeetingEntity) -> Unit
) {
  var meetingNumber by remember { mutableStateOf("اجلاس نمبر 1") }
  var venue by remember { mutableStateOf("مسجد ہال") }
  var attendees by remember { mutableStateOf("انتظامی کمیٹی اراکین") }
  var agenda by remember { mutableStateOf("") }
  var decisions by remember { mutableStateOf("") }
  var assignedPerson by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
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
        Text("نیا انتظامی اجلاس درج کریں", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = IslamicGreen)

        OutlinedTextField(
          value = meetingNumber,
          onValueChange = { meetingNumber = it },
          label = { Text("اجلاس نمبر") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = agenda,
          onValueChange = { agenda = it },
          label = { Text("ایجنڈا *") },
          placeholder = { Text("مثلاً: رمضان انتظامات، سولر مرمت") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = decisions,
          onValueChange = { decisions = it },
          label = { Text("فیصلے و تجاویز *") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = assignedPerson,
          onValueChange = { assignedPerson = it },
          label = { Text("ذمہ دار شخص") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        Button(
          onClick = {
            if (agenda.isNotBlank() && decisions.isNotBlank()) {
              onSubmit(
                MeetingEntity(
                  id = UUID.randomUUID().toString(),
                  mosqueId = mosqueId,
                  meetingNumber = meetingNumber,
                  date = System.currentTimeMillis(),
                  venue = venue,
                  attendees = attendees,
                  agenda = agenda,
                  decisions = decisions,
                  assignedPerson = assignedPerson,
                  status = MeetingStatus.COMPLETED
                )
              )
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("اجلاس محفوظ کریں")
        }
      }
    }
  }
}
