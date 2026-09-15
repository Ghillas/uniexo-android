package com.unicofrance.uniexo.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DefaultSpacer() {
    Spacer(
        modifier = Modifier
            .height(10.dp)
    )
}