package com.example.keepsake

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import com.example.keepsake.ui.theme.FadedTerracotta
import com.example.keepsake.ui.theme.InkBrown
import com.example.keepsake.ui.theme.PaperCream
import com.example.keepsake.ui.theme.Terracotta
import kotlinx.coroutines.launch

/**
 * Widget Settings tab
 */
@Composable
fun WidgetSettingsScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedShape by remember { mutableStateOf(loadFrameShape(context)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PaperCream)
            .padding(paddingValues)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Frame style",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            color = InkBrown
        )
        Text(
            text = "Pick a style — your home screen widget updates instantly.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = InkBrown.copy(alpha = 0.7f)
        )

        FrameShapePicker(
            selected = selectedShape,
            onSelect = { shape ->
                selectedShape = shape
                saveFrameShape(context, shape)
                coroutineScope.launch { PhotoWidget.updateAll(context) }
            }
        )
    }
}

@Composable
private fun FrameShapePicker(
    selected: FrameShape,
    onSelect: (FrameShape) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FrameShape.entries.forEach { shape ->
            val isSelected = shape == selected
            Surface(
                onClick = { onSelect(shape) },
                shape = RoundedCornerShape(50),
                color = if (isSelected) Terracotta else FadedTerracotta.copy(alpha = 0.4f),
                contentColor = if (isSelected) Color.White else InkBrown
            ) {
                Text(
                    text = shape.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}