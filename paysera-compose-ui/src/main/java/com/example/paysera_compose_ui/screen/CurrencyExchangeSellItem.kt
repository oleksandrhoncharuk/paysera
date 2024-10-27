package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_compose_ui.model.CurrencyStateItem
import com.example.paysera_compose_ui.model.CurrencyStateItemSaver
import com.example.paysera_compose_ui.model.getSortedBalanceList

@Composable
fun CurrencyExchangeSellItem(
  currencyState: CurrencyState,
  currencyStateUpdate: (CurrencyStateItem?) -> Unit
) {

  val sellItemText = rememberSaveable {
    mutableStateOf(currencyState.sellStateItem?.operationalAmount ?: "0.00")
  }

  if (currencyState.sellStateItem == null) {
    CircularProgressIndicator()
  } else {
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
          painter = painterResource(id = R.drawable.circle_arrow_up_solid),
          contentDescription = "Currency Exchange Icon Sell"
        )
        Text(
          text = stringResource(R.string.sell_text),
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Row(
        modifier = Modifier
          .width(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
      ) {
        BasicTextField(
          value = sellItemText.value,
          onValueChange = { newText: String ->
            sellItemText.value = newText
            currencyStateUpdate(currencyState.sellStateItem.copy(operationalAmount = newText))
          },
          modifier = Modifier
            .weight(1f)
            .padding(end = 10.dp),
          textStyle = TextStyle(
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          ),
          keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
          ),
          singleLine = true
        )

        DropDownCurrency(
          balance = currencyState.getSortedBalanceList(),
          currencyStateItem = currencyState.sellStateItem,
          currencyStateUpdate = currencyStateUpdate
        )
      }
    }
  }
}
