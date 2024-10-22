package com.example.paysera_compose_ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.theme.BlueGrey

@Composable
fun PayseraTitle(title: String, modifier: Modifier) {
  Text(
    text = title,
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
    modifier = modifier,
    color = BlueGrey
  )
}