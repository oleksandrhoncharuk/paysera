package com.example.paysera_compose_ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.mapToCurrencyDataItem
import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_compose_ui.model.isNullOrEmpty
import com.example.paysera_core.viewmodel.MainViewModel

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
