package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun CurrencyExchangeSellItem(
  currencyState: CurrencyState,
  currencyList: List<String>?,
  currencyStateUpdate: (CurrencyState) -> Unit
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
      modifier = Modifier.wrapContentWidth(Alignment.End),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
      BasicTextField(
        value = currencyState.sellAmount,
        onValueChange = { newText: String ->
          val newState = currencyState.copy(sellAmount = newText)
          currencyStateUpdate(newState)
        },
        modifier = Modifier
          .padding(0.dp)
          .width(80.dp),
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
    }

    DropDownCurrency(currencyList = currencyList ?: listOf("USD", "EUR"), currencyState, currencyStateUpdate)
  }
}
