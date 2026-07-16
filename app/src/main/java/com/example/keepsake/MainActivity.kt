package com.example.keepsake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import com.example.keepsake.ui.theme.KeepsakeTheme
import com.example.keepsake.ui.theme.PaperCream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasExactAlarmPermission(this)) {
            requestExactAlarmPermission(this) // sends user to system Settings to grant it
        }
        scheduleNextRotation(applicationContext)
        setContent {
            KeepsakeTheme {
                Surface(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    AppTabs()
                }
            }
        }
    }
}

private val tabTitles = listOf("Photos", "Widget Settings")

@Composable
private fun AppTabs() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TabRow(selectedTabIndex = selectedTab, containerColor = PaperCream) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { androidx.compose.material3.Text(title, fontFamily = FontFamily.Serif) }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> PhotoPickerScreen(paddingValues = paddingValues)
            1 -> WidgetSettingsScreen(paddingValues = paddingValues)
        }
    }
}