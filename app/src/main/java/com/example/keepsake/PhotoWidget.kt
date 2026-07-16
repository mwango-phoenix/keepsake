package com.example.keepsake

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.compose.runtime.Composable
import androidx.exifinterface.media.ExifInterface
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.action.actionStartActivity

/** Glance-state key for the currently displayed photo index (per widget instance). */
internal val PHOTO_INDEX_KEY = intPreferencesKey("photo_index")
internal val DIRECTION_KEY = ActionParameters.Key<Int>("direction")

private fun normalizeIndex(raw: Int, size: Int): Int =
    if (size <= 0) 0 else ((raw % size) + size) % size

/**
 * Slideshow widget: shows one photo at a time from the saved list.
 */
object PhotoWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val paths = loadPhotoPaths(context)
            val index = normalizeIndex(currentState(PHOTO_INDEX_KEY) ?: 0, paths.size)
            val shape = loadFrameShape(context)

            WidgetContent(paths, index, shape)
        }
    }

    @Composable
    private fun WidgetContent(paths: List<String>, index: Int, shape: FrameShape) {
        val openAppModifier = GlanceModifier
            .fillMaxSize()
            .clickable(actionStartActivity<MainActivity>())

        if (paths.isEmpty()) {
            Box(modifier = openAppModifier) {
                Text(text = "Open Keepsake to add photos")
            }
            return
        }

        val bitmap = decodeSampledBitmap(paths[index], reqSize = 400)
            ?.let { applyFrameShape(it, shape) }
        if (bitmap == null) {
            Box(modifier = openAppModifier) {
                Text(text = "Couldn't load photo")
            }
            return
        }

        Column(modifier = GlanceModifier.fillMaxSize()) {
            Image(
                provider = ImageProvider(bitmap),
                contentDescription = "Keepsake photo — tap to open app",
                contentScale = if (shape == FrameShape.RECTANGLE) ContentScale.Crop else ContentScale.Fit,
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
                    .clickable(actionStartActivity<MainActivity>())
            )

            Row(modifier = GlanceModifier.fillMaxWidth().height(32.dp)) {
                NavButton(symbol = "‹", direction = -1, modifier = GlanceModifier.defaultWeight())
                NavButton(symbol = "›", direction = 1, modifier = GlanceModifier.defaultWeight())
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun NavButton(symbol: String, direction: Int, modifier: GlanceModifier) {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .background(ColorProvider(R.color.widget_nav_scrim))
                .clickable(
                    actionRunCallback<NavigatePhotoAction>(actionParametersOf(DIRECTION_KEY to direction))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = symbol, style = androidx.glance.text.TextStyle(color = ColorProvider(R.color.white)))
        }
    }
}

class NavigatePhotoAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val direction = parameters[DIRECTION_KEY] ?: 1
        val size = loadPhotoPaths(context).size
        if (size == 0) return

        updateAppWidgetState(context, glanceId) { prefs ->
            val current = prefs[PHOTO_INDEX_KEY] ?: 0
            prefs[PHOTO_INDEX_KEY] = normalizeIndex(current + direction, size)
        }
        PhotoWidget.update(context, glanceId)
    }
}

internal suspend fun advanceAllPhotoWidgets(context: Context) {
    val size = loadPhotoPaths(context).size
    if (size == 0) return

    val ids = GlanceAppWidgetManager(context).getGlanceIds(PhotoWidget::class.java)
    ids.forEach { glanceId ->
        updateAppWidgetState(context, glanceId) { prefs ->
            val current = prefs[PHOTO_INDEX_KEY] ?: 0
            prefs[PHOTO_INDEX_KEY] = normalizeIndex(current + 1, size)
        }
    }
    PhotoWidget.updateAll(context)
}

/**
 * Applies the chosen frame shape
 */
private fun applyFrameShape(source: Bitmap, shape: FrameShape): Bitmap {
    return when (shape) {
        FrameShape.RECTANGLE -> source
        FrameShape.CIRCLE -> maskToCircle(source)
        FrameShape.POLAROID -> addPolaroidBorder(source)
    }
}

private fun maskToCircle(source: Bitmap): Bitmap {
    val size = minOf(source.width, source.height)
    val output = createBitmap(size, size)
    val canvas = android.graphics.Canvas(output)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    val radius = size / 2f

    canvas.drawCircle(radius, radius, radius, paint)
    paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)

    val left = (source.width - size) / 2
    val top = (source.height - size) / 2
    val cropped = Bitmap.createBitmap(source, left, top, size, size)
    canvas.drawBitmap(cropped, 0f, 0f, paint)
    return output
}

private fun addPolaroidBorder(source: Bitmap): Bitmap {
    val border = (source.width * 0.06f).toInt()
    val bottomBorder = (source.width * 0.18f).toInt() // classic polaroid: thicker bottom strip
    val outputWidth = source.width + border * 2
    val outputHeight = source.height + border + bottomBorder

    val output = createBitmap(outputWidth, outputHeight)
    val canvas = android.graphics.Canvas(output)
    canvas.drawColor(android.graphics.Color.WHITE)
    canvas.drawBitmap(source, border.toFloat(), border.toFloat(), null)
    return output
}

private fun decodeSampledBitmap(path: String, reqSize: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(path, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    var sampleSize = 1
    while (bounds.outWidth / (sampleSize * 2) >= reqSize && bounds.outHeight / (sampleSize * 2) >= reqSize) {
        sampleSize *= 2
    }

    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    val decoded = BitmapFactory.decodeFile(path, options) ?: return null
    return decoded.rotateToMatchExif(path)
}

private fun Bitmap.rotateToMatchExif(path: String): Bitmap {
    val degrees = try {
        when (ExifInterface(path).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    } catch (_: Exception) {
        0f
    }
    if (degrees == 0f) return this
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}