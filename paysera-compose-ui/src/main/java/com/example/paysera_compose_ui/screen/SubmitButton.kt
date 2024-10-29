package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.R

@Composable
fun SubmitButton(
  onSubmitClick: () -> Unit
) {
  Button(
    onClick = { onSubmitClick() },
    modifier = Modifier
      .padding(16.dp)
      .fillMaxWidth()
      .height(60.dp)
      .imePadding()
  ) {
    Text(
      text = stringResource(R.string.submit_button_text),
      fontSize = 20.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}
