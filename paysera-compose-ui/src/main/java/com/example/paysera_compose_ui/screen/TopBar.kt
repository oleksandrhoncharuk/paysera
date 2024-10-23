package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.theme.Blue19

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = Blue19,
      titleContentColor = Color.White,
    ),
    title = {
      Text(stringResource(R.string.toolbar_title), fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
    }
  )
}