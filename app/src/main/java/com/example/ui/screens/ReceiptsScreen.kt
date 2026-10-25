package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MosqueEntity
import com.example.data.model.ReceiptEntity
import com.example.data.model.TransactionStatus
import com.example.ui.components.OfficialReceiptCard
import com.example.ui.theme.*
import com.example.util.DateUtils
import com.example.util.UrduNumberHelper

@Composable
fun ReceiptsScreen(
  mosque: MosqueEntity?,
  receipts: List<ReceiptEntity>,
  onCancelReceipt: (ReceiptEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var selectedReceipt by remember { mutableStateOf<ReceiptEntity?>(null) }
  var filterConfirmedOnly by remember { mutableStateOf(false) }

  val filteredReceipts = receipts.filter { r ->
    val matchesSearch = searchQuery.isBlank() ||
      r.receiptNumber.contains(searchQuery, ignoreCase = true) ||
      r.donorName.contains(searchQuery, ignoreCase = true) ||
      r.donorPhone.contains(searchQuery)
    val matchesStatus = !filterConfirmedOnly || r.status == TransactionStatus.CONFIRMED
    matchesSearch && matchesStatus
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "رسیدوں کا مکمل نظام",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = IslamicGreen
      )

      FilterChip(
        selected = filterConfirmedOnly,
        onClick = { filterConfirmedOnly = !filterConfirmedOnly },
        label = { Text("صرف تصدیق شدہ") },
        shape = RoundedCornerShape(8.dp)
      )
    }

    Text(
      text = "کل رسیدیں: ${receipts.size} • ہر مسجد کا اپنا خودکار یونیک سیریل نمبر",
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("رسید نمبر، نام یا موبائل سے تلاش کریں...") },
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
        .testTag("receipt_search_input"),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    if (filteredReceipts.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(40.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MatteGold, modifier = Modifier.size(50.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "کوئی رسید نہیں ملی",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredReceipts) { receipt ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedReceipt = receipt }
              .testTag("receipt_list_item_${receipt.receiptNumber}"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (receipt.status == TransactionStatus.CANCELLED)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
              else MaterialTheme.colorScheme.surface
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (receipt.status == TransactionStatus.CANCELLED) Color(0xFFFFCDD2) else IslamicGreenContainer
                  ) {
                    Text(
                      text = receipt.receiptNumber,
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = if (receipt.status == TransactionStatus.CANCELLED) ErrorRed else OnIslamicGreenContainer,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  if (receipt.status == TransactionStatus.CANCELLED) {
                    Text(
                      text = "(منسوخ شدہ)",
                      style = MaterialTheme.typography.labelSmall,
                      color = ErrorRed
                    )
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = receipt.donorName,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "${receipt.purpose} • ${DateUtils.formatUrduDate(receipt.issueDate)}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = UrduNumberHelper.formatAmount(receipt.amount),
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = if (receipt.status == TransactionStatus.CANCELLED) MaterialTheme.colorScheme.onSurfaceVariant else SuccessGreen
                )
                Text(
                  text = "تفصیل دیکھیں >",
                  style = MaterialTheme.typography.labelSmall,
                  color = IslamicGreen
                )
              }
            }
          }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
      }
    }
  }

  // Detailed Receipt Dialog
  selectedReceipt?.let { receipt ->
    Dialog(onDismissRequest = { selectedReceipt = null }) {
      OfficialReceiptCard(
        mosque = mosque,
        receipt = receipt,
        onShareClick = {
          val shareText = buildString {
            appendLine("🕌 *${mosque?.name ?: "جامع مسجد"}*")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine("📄 *رسید نمبر:* ${receipt.receiptNumber}")
            appendLine("📅 *تاریخ:* ${DateUtils.formatUrduDateTime(receipt.issueDate)}")
            appendLine("👤 *محترم جناب:* ${receipt.donorName}")
            if (receipt.donorPhone.isNotBlank()) appendLine("📱 *موبائل:* ${receipt.donorPhone}")
            appendLine("💰 *رقم:* ${UrduNumberHelper.formatAmount(receipt.amount)}")
            appendLine("📝 *الفاظ میں:* ${receipt.amountInWords}")
            appendLine("🎯 *مقصد:* ${receipt.purpose}")
            appendLine("💳 *طریقہ ادائیگی:* ${receipt.paymentMethodUrdu}")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine(mosque?.receiptNote ?: "اللہ تعالیٰ آپ کے تعاون کو قبول فرمائے۔ آمین۔")
            appendLine("منتظمین: ${mosque?.name}")
          }
          val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
          }
          val shareIntent = Intent.createChooser(sendIntent, "رسید شیئر کریں")
          context.startActivity(shareIntent)
        },
        onCancelClick = {
          onCancelReceipt(receipt)
          selectedReceipt = null
        }
      )
    }
  }
}
