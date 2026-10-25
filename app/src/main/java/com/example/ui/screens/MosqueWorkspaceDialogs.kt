package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MosqueEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchMosqueBottomSheet(
  allMosques: List<MosqueEntity>,
  activeMosqueId: String,
  onSelectMosque: (String) -> Unit,
  onCreateNewClick: () -> Unit,
  onJoinClick: () -> Unit,
  onDismiss: () -> Unit
) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surface,
    dragHandle = { BottomSheetDefaults.DragHandle() }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "مسجد کا انتخاب یا تبدیلی",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = IslamicGreen
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Text(
        text = "ہر مسجد کا الگ نام، لوگو اور مکمل محفوظ ڈیٹا ہے۔ نیچے سے متعلقہ مسجد کا انتخاب کریں:",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(16.dp))

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 280.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(allMosques) { mosque ->
          val isSelected = mosque.id == activeMosqueId
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onSelectMosque(mosque.id)
                onDismiss()
              }
              .testTag("mosque_item_${mosque.id}"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) IslamicGreenContainer else MaterialTheme.colorScheme.surfaceVariant
            ),
            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(IslamicGreen)
            ) else null
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
                    .background(if (isSelected) IslamicGreen else MatteGold),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Mosque,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(20.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = mosque.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) OnIslamicGreenContainer else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${mosque.code} • ${mosque.city} • ${mosque.phone}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Active",
                  tint = IslamicGreen
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = {
            onDismiss()
            onCreateNewClick()
          },
          modifier = Modifier
            .weight(1f)
            .testTag("create_new_mosque_button"),
          colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("نئی مسجد بنائیں", style = MaterialTheme.typography.labelMedium)
        }

        OutlinedButton(
          onClick = {
            onDismiss()
            onJoinClick()
          },
          modifier = Modifier
            .weight(1f)
            .testTag("join_mosque_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("کوڈ سے شامل ہوں", style = MaterialTheme.typography.labelMedium)
        }
      }
    }
  }
}

@Composable
fun CreateMosqueDialog(
  onDismiss: () -> Unit,
  onSubmit: (
    name: String,
    shortName: String,
    code: String,
    address: String,
    city: String,
    phone: String,
    email: String,
    adminName: String,
    adminEmail: String,
    adminPhone: String
  ) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var shortName by remember { mutableStateOf("") }
  var code by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var city by remember { mutableStateOf("لاہور") }
  var phone by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var adminName by remember { mutableStateOf("") }
  var adminEmail by remember { mutableStateOf("") }
  var adminPhone by remember { mutableStateOf("") }

  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
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
              text = "نئی مسجد کا اندراج",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = IslamicGreen
            )
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }
          Text(
            text = "اپنی مسجد کا الگ محفوظ ورکسپیس بنائیں جس کے تمام مالی ریکارڈ، رسیدیں اور نام مکمل الگ ہوں گے۔",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))
        }

        item {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("مسجد کا مکمل نام *") },
            placeholder = { Text("مثلاً: جامع مسجد نورانیہ رضویہ") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_mosque_name_input"),
            shape = RoundedCornerShape(12.dp)
          )
        }

        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = shortName,
              onValueChange = { shortName = it },
              label = { Text("مختصر نام") },
              placeholder = { Text("جامع مسجد نورانیہ") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = code,
              onValueChange = { code = it.uppercase() },
              label = { Text("رسید کوڈ (3-4 حروف)") },
              placeholder = { Text("JMN") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }

        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = city,
              onValueChange = { city = it },
              label = { Text("شہر *") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("مسجد کا رابطہ نمبر *") },
              placeholder = { Text("0300-1234567") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }

        item {
          OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("مسجد کا مکمل پتہ") },
            placeholder = { Text("محلہ، گلی، نزد معروف مقام") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }

        item {
          HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
          Text(
            text = "سپر ایڈمن کے کوائف (مسجد بنانے والا)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MatteGoldDark
          )
        }

        item {
          OutlinedTextField(
            value = adminName,
            onValueChange = { adminName = it },
            label = { Text("سپر ایڈمن کا نام *") },
            placeholder = { Text("مثلاً: محمد اکرم") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }

        item {
          OutlinedTextField(
            value = adminPhone,
            onValueChange = { adminPhone = it },
            label = { Text("ایڈمن کا موبائل نمبر") },
            placeholder = { Text("03xx-xxxxxxx") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }

        errorMessage?.let { err ->
          item {
            Text(
              text = err,
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodySmall
            )
          }
        }

        item {
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = {
              if (name.isBlank()) {
                errorMessage = "مسجد کا مکمل نام درج کرنا لازمی ہے۔"
                return@Button
              }
              if (city.isBlank()) {
                errorMessage = "شہر درج کرنا لازمی ہے۔"
                return@Button
              }
              onSubmit(
                name, shortName, code, address, city, phone, email,
                adminName, adminEmail, adminPhone
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("submit_create_mosque_button"),
            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Done, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("مسجد کا ورکسپیس بنائیں", style = MaterialTheme.typography.labelLarge)
          }
        }
      }
    }
  }
}

@Composable
fun JoinMosqueDialog(
  onDismiss: () -> Unit,
  onSubmit: (code: String, name: String, email: String, phone: String) -> Unit
) {
  var inviteCode by remember { mutableStateOf("") }
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "موجودہ مسجد میں شمولیت",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = IslamicGreen
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Text(
          text = "مسجد کے ایڈمن کا جاری کردہ 6 ہندسوں کا دعوتی کوڈ درج کریں تاکہ آپ متعلقہ رول کے ساتھ شامل ہو سکیں۔",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
          value = inviteCode,
          onValueChange = { inviteCode = it.uppercase() },
          label = { Text("دعوتی کوڈ (Invite Code) *") },
          placeholder = { Text("مثلاً: HMR-FIN یا JMN-REC") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("invite_code_input"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("آپ کا نام *") },
          placeholder = { Text("مثلاً: محمد عثمان") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("آپ کا موبائل نمبر *") },
          placeholder = { Text("03xx-xxxxxxx") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        errorMessage?.let {
          Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
          onClick = {
            if (inviteCode.isBlank() || name.isBlank()) {
              errorMessage = "دعوتی کوڈ اور آپ کا نام لازمی ہیں۔"
              return@Button
            }
            onSubmit(inviteCode, name, email, phone)
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("submit_join_mosque_button"),
          colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.GroupAdd, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("مسجد میں شامل ہوں", style = MaterialTheme.typography.labelLarge)
        }
      }
    }
  }
}
