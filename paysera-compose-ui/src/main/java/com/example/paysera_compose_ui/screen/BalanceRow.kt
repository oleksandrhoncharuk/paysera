package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_compose_ui.model.isBalanceNullOrEmpty
import com.example.paysera_database.model.roundAmount

@Composable
fun BalanceRow(state: CurrencyState?) {
  if (state == null || state.isBalanceNullOrEmpty()) {
    Text(
      text = stringResource(R.string.no_balance_available),
      color = Color.Gray,
      fontSize = 16.sp,
      modifier = Modifier.padding(top = 15.dp)
    )
  } else {
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(40.dp),
      modifier = Modifier.padding(top = 15.dp)
    ) {
      items(state.balance!!.entries.toList()) { currency ->
        Text(
          text = "${currency.key} ${currency.value.roundAmount()}",
          color = Color.Black,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}