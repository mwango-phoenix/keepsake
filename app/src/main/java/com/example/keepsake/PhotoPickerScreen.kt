package com.example.keepsake

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.edit

/**
 * Main screen: lets the user pick photos for the slideshow widget
 */
@Composable
fun PhotoPickerScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var photoPaths by remember { mutableStateOf(loadPhotoPaths(context)) }
    fun setPhotoPaths(paths: List<String>) {
        photoPaths = paths
        savePhotoPaths(context, paths)
    }

    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 30)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                val newPaths = copyPhotosToInternalStorage(context, uris)
                setPhotoPaths(photoPaths + newPaths)
                PhotoWidget.updateAll(context)
            }
        }
    }

    fun launchPicker() {
        pickMultipleMediaLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PaperCream)
            .padding(paddingValues)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (photoPaths.isEmpty()) {
                EmptyState(
                    modifier = Modifier.align(Alignment.Center),
                    onPickClick = ::launchPicker
                )
            } else {
                PhotoGrid(
                    photoPaths = photoPaths,
                    onRemove = { path ->
                        File(path).delete()
                        setPhotoPaths(photoPaths.filterNot { it == path })
                        coroutineScope.launch { PhotoWidget.updateAll(context) }
                    },
                    onAddMoreClick = ::launchPicker
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier, onPickClick: () -> Unit) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Your keepsake box is empty",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            color = InkBrown
        )
        Text(
            text = "Pick a handful of photos to bring to your\nhome screen slideshow.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = InkBrown.copy(alpha = 0.7f)
        )
        PhotoPickerButton(onClick = onPickClick)
    }
}

// internal, not private: PhotoWidget.kt reads the same prefs from the widget process.
internal const val PREFS_NAME = "keepsake_prefs"
internal const val KEY_PHOTO_PATHS = "photo_paths"
internal const val PATH_DELIMITER = "|||"
internal const val KEY_FRAME_SHAPE = "frame_shape"

internal enum class FrameShape { RECTANGLE, CIRCLE, POLAROID }

internal fun loadFrameShape(context: Context): FrameShape {
    val name = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(KEY_FRAME_SHAPE, null)
    return FrameShape.entries.find { it.name == name } ?: FrameShape.RECTANGLE
}

internal fun saveFrameShape(context: Context, shape: FrameShape) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit(commit = true) {
            putString(KEY_FRAME_SHAPE, shape.name)
        }
}

internal fun loadPhotoPaths(context: Context): List<String> {
    val raw = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(KEY_PHOTO_PATHS, null) ?: return emptyList()
    return raw.split(PATH_DELIMITER).filter { it.isNotBlank() }
}

private fun savePhotoPaths(context: Context, paths: List<String>) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit {
            putString(KEY_PHOTO_PATHS, paths.joinToString(PATH_DELIMITER))
        }
}

/**
 * Copies each picked image into filesDir/photos/
 */
private suspend fun copyPhotosToInternalStorage(context: Context, sourceUris: List<Uri>): List<String> {
    val photosDir = File(context.filesDir, "photos").apply { mkdirs() }
    val savedPaths = mutableListOf<String>()

    for (uri in sourceUris) {
        try {
            val destFile = File(photosDir, "widget_photo_${System.currentTimeMillis()}_${savedPaths.size}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            savedPaths.add(destFile.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return savedPaths
}