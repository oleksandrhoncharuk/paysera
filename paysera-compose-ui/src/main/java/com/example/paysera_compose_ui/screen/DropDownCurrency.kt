package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.model.CurrencyState

@Composable
fun DropDownCurrency(
  currencyList: List<String>,
  currencyToOperate: CurrencyState,
  currencyStateUpdate: (CurrencyState) -> Unit
) {
  val isDropDownExpanded = remember {
    mutableStateOf(false)
  }

  val itemPosition = remember {
    mutableStateOf(0)
  }

  Box {
    Row(
      horizontalArrangement = Arrangement.End,
      modifier = Modifier.clickable {
        isDropDownExpanded.value = true
      }
    ) {
      Text(currencyList[itemPosition.value], fontSize = 18.sp, fontWeight = FontWeight.Bold)
      Image(
        painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
        contentDescription = "DropDown Icon"
      )
    }
    DropdownMenu(
      expanded = isDropDownExpanded.value,
      onDismissRequest = {
        isDropDownExpanded.value = false
      }) {
      currencyList.forEachIndexed { index, currencyName ->
        DropdownMenuItem(text = {
          Text(text = currencyName)
          currencyStateUpdate(currencyToOperate.copy(currencySellName = currencyName))
        },
          onClick = {
            isDropDownExpanded.value = false
            itemPosition.value = index
          })
      }
    }
  }
}