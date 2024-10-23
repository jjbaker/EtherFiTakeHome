package com.etherfi.takehome.view.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.etherfi.takehome.ui.theme.Typography

@Composable
fun EnabledDisabledButtonWithSpinner(
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    isDisabledClick: () -> Unit = {},
    isSpinning: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        EnabledDisabledButton(
            text = text,
            onClick = onClick,
            isEnabled = isEnabled,
            isDisabledClick = isDisabledClick,
        )
        if (isSpinning) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun EnabledDisabledButton(
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    isDisabledClick: () -> Unit = {}
) {
    Button(
        onClick = { if (isEnabled) onClick() else isDisabledClick() },
        modifier = Modifier.padding(10.dp),
        colors = if (isEnabled) {
            ButtonDefaults.buttonColors()
        } else {
            with(ButtonDefaults.buttonColors()) {
                ButtonDefaults.buttonColors(
                    containerColor = disabledContainerColor,
                    contentColor = disabledContentColor
                )
            }
        }
    ) {
        Text(
            text = text,
            style = Typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
    }
}