package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.model.CurrencyStateItem

@Composable
fun DropDownCurrency(
  balance: List<String>,
  currencyStateItem: CurrencyStateItem,
  currencyStateUpdate: (CurrencyStateItem?) -> Unit
) {
  val isDropDownExpanded = remember {
    mutableStateOf(false)
  }

  val itemCurrencyName = rememberSaveable {
    mutableStateOf(balance[0])
  }

  Box {
    Row(
      modifier = Modifier
        .clickable {
          isDropDownExpanded.value = true
        }
        .wrapContentWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (balance.isNotEmpty()) {
        Text(itemCurrencyName.value, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp))
        Image(
          painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
          contentDescription = "DropDown Icon"
        )
      }
    }
    DropdownMenu(
      expanded = isDropDownExpanded.value,
      onDismissRequest = {
        isDropDownExpanded.value = false
      }) {
      balance.forEach { currencyName ->
        DropdownMenuItem(text = {
          Text(text = currencyName)
        },
          onClick = {
            isDropDownExpanded.value = false
            itemCurrencyName.value = currencyName
            currencyStateUpdate(currencyStateItem.copy(currencyName = currencyName))
          }
        )
      }
    }
  }
}
