package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MosqueEntity
import com.example.data.model.ReceiptEntity
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.util.DateUtils
import com.example.util.UrduNumberHelper

@Composable
fun MosqueTopBar(
  activeMosque: MosqueEntity?,
  activeRole: UserRole,
  onSwitchMosqueClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.primaryContainer,
    tonalElevation = 4.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .weight(1f)
          .clickable { onSwitchMosqueClick() }
          .testTag("switch_mosque_top_bar")
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(listOf(IslamicGreen, IslamicGreenDark))
            )
            .border(1.5.dp, MatteGold, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Mosque,
            contentDescription = "Mosque Icon",
            tint = MatteGoldLight,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = activeMosque?.name ?: "مسجد منتخب نہیں",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MatteGold.copy(alpha = 0.2f)
            ) {
              Text(
                text = activeRole.urduTitle,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MatteGoldDark,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "• ${activeMosque?.city ?: "پاکستان"}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
          }
        }
      }

      FilledTonalButton(
        onClick = onSwitchMosqueClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = MatteGoldContainer,
          contentColor = OnMatteGoldContainer
        ),
        modifier = Modifier.testTag("change_workspace_button")
      ) {
        Icon(
          imageVector = Icons.Default.SwapHoriz,
          contentDescription = "Switch",
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("مسجد بدلیں", style = MaterialTheme.typography.labelMedium)
      }
    }
  }
}

@Composable
fun StatCard(
  title: String,
  amount: Double,
  icon: ImageVector,
  accentColor: Color,
  subtitle: String? = null,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.horizontalGradient(
        listOf(accentColor.copy(alpha = 0.3f), MaterialTheme.colorScheme.outline)
      )
    )
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = UrduNumberHelper.formatAmount(amount),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      subtitle?.let {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = it,
          style = MaterialTheme.typography.labelSmall,
          color = accentColor.copy(alpha = 0.9f)
        )
      }
    }
  }
}

@Composable
fun OfficialReceiptCard(
  mosque: MosqueEntity?,
  receipt: ReceiptEntity,
  onShareClick: () -> Unit,
  onCancelClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.linearGradient(listOf(MatteGold, IslamicGreen))
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      // Receipt Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = mosque?.name ?: "جامع مسجد",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = IslamicGreen
          )
          Text(
            text = "${mosque?.address ?: ""}، ${mosque?.city ?: ""}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          mosque?.phone?.takeIf { it.isNotBlank() }?.let {
            Text(
              text = "رابطہ نمبر: $it",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = IslamicGreenContainer
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "رسید نمبر",
              style = MaterialTheme.typography.labelSmall,
              color = OnIslamicGreenContainer
            )
            Text(
              text = receipt.receiptNumber,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = OnIslamicGreenContainer
            )
          }
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
      )

      // Donor & Amount Info
      ReceiptRow(label = "محترم جناب:", value = receipt.donorName)
      if (receipt.donorPhone.isNotBlank()) {
        ReceiptRow(label = "موبائل نمبر:", value = receipt.donorPhone)
      }
      if (receipt.donorAddress.isNotBlank()) {
        ReceiptRow(label = "پتہ:", value = receipt.donorAddress)
      }
      ReceiptRow(label = "مد / مقصد:", value = receipt.purpose)
      ReceiptRow(label = "ادائیگی کا طریقہ:", value = receipt.paymentMethodUrdu)
      ReceiptRow(label = "تاریخ و وقت:", value = DateUtils.formatUrduDateTime(receipt.issueDate))

      Spacer(modifier = Modifier.height(10.dp))

      // Amount Box
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MatteGoldContainer.copy(alpha = 0.6f),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(MatteGold, MatteGoldDark))
        )
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "موصولہ رقم: " + UrduNumberHelper.formatAmount(receipt.amount),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = OnMatteGoldContainer
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "الفاظ میں: " + receipt.amountInWords,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = OnMatteGoldContainer,
            textAlign = TextAlign.Center
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Dua & Note
      Text(
        text = mosque?.receiptNote ?: "اللہ تعالیٰ آپ کے تعاون کو قبول فرمائے۔ آمین۔",
        style = MaterialTheme.typography.bodySmall,
        color = IslamicGreenDark,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Footer: Issuer and Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "جاری کنندہ: ${receipt.issuedBy}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          if (onCancelClick != null && receipt.status != com.example.data.model.TransactionStatus.CANCELLED) {
            OutlinedButton(
              onClick = onCancelClick,
              colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("cancel_receipt_button")
            ) {
              Text("منسوخ کریں", style = MaterialTheme.typography.labelSmall)
            }
          }

          Button(
            onClick = onShareClick,
            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("share_receipt_button")
          ) {
            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("شیئر رسید", style = MaterialTheme.typography.labelSmall)
          }
        }
      }
    }
  }
}

@Composable
fun ReceiptRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.End
    )
  }
}
