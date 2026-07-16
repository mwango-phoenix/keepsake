package com.example.keepsake

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keepsake.ui.theme.Terracotta

/**
 * Reusable pill-shaped button
 */
@Composable
fun PhotoPickerButton(
    modifier: Modifier = Modifier,
    label: String = "Pick Photos",
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Terracotta,
            contentColor = androidx.compose.ui.graphics.Color.White
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp
        )
    }
}