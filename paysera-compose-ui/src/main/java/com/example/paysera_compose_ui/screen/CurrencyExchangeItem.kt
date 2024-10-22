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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.theme.LightGreen

@Composable
fun CurrencyExchangeItem(isSell: Boolean, operationAmount: Double, currency: String) {
  var text = remember { mutableStateOf("0") }

  val painterResource = if (isSell)
    painterResource(id = R.drawable.circle_arrow_up_solid)
  else
    painterResource(id = R.drawable.circle_arrow_down_solid)

  val operationAmountText = if (isSell) operationAmount.toString() else "+$operationAmount"
  val operationalAmountTextColor = if (isSell) Color.Black else LightGreen

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
      Image(painter = painterResource, contentDescription = "Currency Exchange Icon")
      Text(
        text = if (isSell) stringResource(R.string.sell_text) else stringResource(R.string.receive_text),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Row(
      modifier = Modifier.wrapContentWidth(Alignment.End),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
      if (isSell) {
        BasicTextField(
          value = text.value,
          onValueChange = { newText: String -> text.value = newText },
          modifier = Modifier
            .padding(0.dp)
            .width(80.dp),
          textStyle = TextStyle(
            color = operationalAmountTextColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          ),
          keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
          ),
          singleLine = true
        )
      } else {
        Text(
          text = operationAmountText,
          color = operationalAmountTextColor,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }
//      DropDownCurrency(currencyList = listOf("USD", "EUR", "BGD", "PLN", "UAH"))
    }
  }
}

@Preview
@Composable
fun CurrencyExchangeItemPreview() {
  CurrencyExchangeItem(isSell = true, operationAmount = 0.0, currency = "USD")
  CurrencyExchangeItem(isSell = false, operationAmount = 100.0, currency = "USD")
}