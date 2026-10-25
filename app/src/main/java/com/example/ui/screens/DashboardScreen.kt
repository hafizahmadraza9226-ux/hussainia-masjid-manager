package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseEntity
import com.example.data.model.IncomeEntity
import com.example.data.model.MosqueEntity
import com.example.ui.DashboardUiState
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import com.example.util.DateUtils
import com.example.util.UrduNumberHelper

@Composable
fun DashboardScreen(
  mosque: MosqueEntity?,
  dashboardState: DashboardUiState,
  recentIncomes: List<IncomeEntity>,
  recentExpenses: List<ExpenseEntity>,
  onAddIncomeClick: () -> Unit,
  onAddExpenseClick: () -> Unit,
  onAddBillClick: () -> Unit,
  onViewReceiptsClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedRecentTab by remember { mutableIntStateOf(0) } // 0: آمدن, 1: اخراجات

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))

      // Main Hero Net Balance Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("net_balance_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(IslamicGreen, MatteGold))
        )
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                listOf(
                  IslamicGreen.copy(alpha = 0.12f),
                  MatteGold.copy(alpha = 0.18f)
                )
              )
            )
            .padding(20.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "دستیاب خالص بیلنس",
                  style = MaterialTheme.typography.titleSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = mosque?.name ?: "جامع مسجد",
                  style = MaterialTheme.typography.labelSmall,
                  color = IslamicGreenDark
                )
              }
              Surface(
                shape = CircleShape,
                color = if (dashboardState.netBalance >= 0) IslamicGreen else ErrorRed
              ) {
                Text(
                  text = if (dashboardState.netBalance >= 0) "مثبت بیلنس" else "منفی بیلنس",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = UrduNumberHelper.formatAmount(dashboardState.netBalance),
              style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp
              ),
              color = if (dashboardState.netBalance >= 0) IslamicGreen else ErrorRed
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "الفاظ میں: " + UrduNumberHelper.convertToUrduWords(dashboardState.netBalance),
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(
              modifier = Modifier.padding(vertical = 12.dp),
              color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = "مجموعی آمدن",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = UrduNumberHelper.formatAmount(dashboardState.totalIncome),
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = SuccessGreen
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "مجموعی اخراجات",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = UrduNumberHelper.formatAmount(dashboardState.totalExpense),
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = ErrorRed
                )
              }
            }
          }
        }
      }
    }

    // Unpaid Bills Alert
    if (dashboardState.pendingBillsCount > 0) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(WarningOrange, MatteGold))
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pending_bills_alert")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                Icons.Default.WarningAmber,
                contentDescription = null,
                tint = WarningOrange,
                modifier = Modifier.size(26.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "${dashboardState.pendingBillsCount} یوٹیلیٹی بل غیر ادا شدہ ہیں",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFFBF360C)
                )
                Text(
                  text = "بقایا رقم: " + UrduNumberHelper.formatAmount(dashboardState.unpaidBillsTotal),
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFFD84315)
                )
              }
            }

            OutlinedButton(
              onClick = onAddBillClick,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFBF360C))
            ) {
              Text("بل دیکھیں", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      }
    }

    // Quick Actions
    item {
      Text(
        text = "فوری کارروائیاں (Quick Actions)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onAddIncomeClick,
          modifier = Modifier
            .weight(1f)
            .testTag("quick_action_add_income"),
          colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(vertical = 12.dp)
        ) {
          Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("نئی آمدن", style = MaterialTheme.typography.labelMedium)
        }

        Button(
          onClick = onAddExpenseClick,
          modifier = Modifier
            .weight(1f)
            .testTag("quick_action_add_expense"),
          colors = ButtonDefaults.buttonColors(containerColor = MatteGoldDark),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(vertical = 12.dp)
        ) {
          Icon(Icons.Default.RemoveCircle, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("نیا خرچ", style = MaterialTheme.typography.labelMedium)
        }

        OutlinedButton(
          onClick = onViewReceiptsClick,
          modifier = Modifier
            .weight(1f)
            .testTag("quick_action_view_receipts"),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(vertical = 12.dp)
        ) {
          Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("رسیدیں", style = MaterialTheme.typography.labelMedium)
        }
      }
    }

    // Financial Cards Grid (Today & Month)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatCard(
          title = "آج کی آمدن",
          amount = dashboardState.todayIncome,
          icon = Icons.Default.TrendingUp,
          accentColor = SuccessGreen,
          subtitle = "آج کی وصولیاں",
          modifier = Modifier.weight(1f)
        )

        StatCard(
          title = "آج کے اخراجات",
          amount = dashboardState.todayExpense,
          icon = Icons.Default.TrendingDown,
          accentColor = ErrorRed,
          subtitle = "آج کے ادائیگیاں",
          modifier = Modifier.weight(1f)
        )
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatCard(
          title = "رواں ماہ آمدن",
          amount = dashboardState.monthIncome,
          icon = Icons.Default.AccountBalanceWallet,
          accentColor = IslamicGreen,
          subtitle = DateUtils.getCurrentMonthUrdu(),
          modifier = Modifier.weight(1f)
        )

        StatCard(
          title = "رواں ماہ اخراجات",
          amount = dashboardState.monthExpense,
          icon = Icons.Default.Payments,
          accentColor = WarningOrange,
          subtitle = DateUtils.getCurrentMonthUrdu(),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Recent Transactions Header & Toggle
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "حالیہ مالی اندراجات",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Row {
          FilterChip(
            selected = selectedRecentTab == 0,
            onClick = { selectedRecentTab = 0 },
            label = { Text("آمدن (${recentIncomes.take(5).size})") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("filter_recent_income")
          )
          Spacer(modifier = Modifier.width(6.dp))
          FilterChip(
            selected = selectedRecentTab == 1,
            onClick = { selectedRecentTab = 1 },
            label = { Text("اخراجات (${recentExpenses.take(5).size})") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("filter_recent_expense")
          )
        }
      }
    }

    if (selectedRecentTab == 0) {
      if (recentIncomes.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(Icons.Default.Savings, contentDescription = null, tint = MatteGold, modifier = Modifier.size(40.dp))
              Spacer(modifier = Modifier.height(8.dp))
              Text("کوئی آمدن ریکارڈ نہیں کی گئی", style = MaterialTheme.typography.bodyMedium)
              Text("نئی آمدن درج کرنے کے لیے اوپر بٹن دبائیں۔", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      } else {
        items(recentIncomes.take(5)) { inc ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("recent_income_${inc.id}"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(IslamicGreenContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = IslamicGreen, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = inc.donorName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${inc.category.urduTitle} • ${DateUtils.formatUrduDate(inc.createdAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "+ " + UrduNumberHelper.formatAmount(inc.amount),
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = SuccessGreen
                )
                Text(
                  text = inc.receiptNumber,
                  style = MaterialTheme.typography.labelSmall,
                  color = MatteGoldDark
                )
              }
            }
          }
        }
      }
    } else {
      if (recentExpenses.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(Icons.Default.Receipt, contentDescription = null, tint = MatteGold, modifier = Modifier.size(40.dp))
              Spacer(modifier = Modifier.height(8.dp))
              Text("کوئی اخراجات ریکارڈ نہیں ہوئے", style = MaterialTheme.typography.bodyMedium)
              Text("نیا خرچ درج کرنے کے لیے اوپر بٹن دبائیں۔", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      } else {
        items(recentExpenses.take(5)) { exp ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("recent_expense_${exp.id}"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEBEE)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = exp.payeeName.ifBlank { exp.category.urduTitle },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${exp.category.urduTitle} • ${DateUtils.formatUrduDate(exp.date)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Text(
                text = "- " + UrduNumberHelper.formatAmount(exp.amount),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = ErrorRed
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(18.dp))
    }
  }
}
