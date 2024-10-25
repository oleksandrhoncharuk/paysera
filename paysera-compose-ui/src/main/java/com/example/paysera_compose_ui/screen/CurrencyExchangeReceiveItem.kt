package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_compose_ui.model.CurrencyStateItem
import com.example.paysera_compose_ui.model.getSortedBalanceList
import com.example.paysera_compose_ui.model.isNullOrEmpty
import com.example.paysera_compose_ui.theme.LightGreen

@Composable
fun CurrencyExchangeReceiveItem(
  currencyState: CurrencyState,
  currencyStateUpdate: (CurrencyStateItem?) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        painter = painterResource(id = R.drawable.circle_arrow_down_solid),
        contentDescription = "Currency Exchange Icon"
      )
      Text(
        text = stringResource(R.string.receive_text),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Row(
      modifier = Modifier.wrapContentWidth(Alignment.End),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
      Text(
        text = currencyState.receiveStateItem?.operationalAmount?.toString() ?: "0.00",
        color = LightGreen,
        fontSize = 18.sp,
        modifier = Modifier.padding(end = 10.dp),
        fontWeight = FontWeight.Bold
      )

        DropDownCurrency(
          balance = currencyState.getSortedBalanceList(),
          currencyStateItem = currencyState.receiveStateItem!!,
          currencyStateUpdate = currencyStateUpdate
        )
    }
  }
}
