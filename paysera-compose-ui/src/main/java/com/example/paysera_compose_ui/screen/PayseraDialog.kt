package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_core.repository.mapper.roundAmount

@Composable
fun PayseraDialog(currencyState: CurrencyState, dismissDialog: () -> Unit) {
  AlertDialog(
    onDismissRequest = {
      dismissDialog()
    },
    title = {
      Text(text = stringResource(R.string.dialog_title))
    },
    text = {
      Text(
        text = stringResource(
          R.string.dialog_message,
          currencyState.sellStateItem?.operationalAmount ?: 0.00,
          currencyState.sellStateItem?.currencyName ?: "",
          currencyState.receiveStateItem?.operationalAmount ?: 0.00,
          currencyState.receiveStateItem?.currencyName ?: "",
          currencyState.sellStateItem?.fee.roundAmount()
        )
      )
    },
    confirmButton = {
      Button(
        onClick = {
          dismissDialog()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(60.dp)
      ) {
        Text(text = stringResource(R.string.dialog_done))
      }
    }
  )
}