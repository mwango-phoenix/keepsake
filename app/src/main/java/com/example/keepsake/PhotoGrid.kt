package com.example.keepsake

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.keepsake.ui.theme.FadedTerracotta
import com.example.keepsake.ui.theme.InkBrown
import com.example.keepsake.ui.theme.Terracotta

/**
 * Displays the selected photos as a scattered, slightly-tilted "polaroid"
 * grid — evokes a physical keepsake box rather than a flat photo gallery.
 * A dashed "add more" tile always sits at the end of the grid.
 */
@Composable
fun PhotoGrid(
    photoPaths: List<String>,
    onRemove: (String) -> Unit,
    onAddMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(photoPaths, key = { it }) { path ->
            // Deterministic tiny tilt per photo, alternating left/right,
            // so the layout feels scattered but never re-shuffles on recompose.
            val tilt = if (path.hashCode() % 2 == 0) -3f else 3f
            PolaroidCard(path = path, tiltDegrees = tilt, onRemove = { onRemove(path) })
        }

        item {
            AddMoreTile(onClick = onAddMoreClick)
        }
    }
}

@Composable
private fun PolaroidCard(path: String, tiltDegrees: Float, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .graphicsLayer { rotationZ = tiltDegrees }
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(4.dp), clip = false)
            .background(Color.White, RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        androidx.compose.foundation.layout.Column {
            AsyncImage(
                model = path,
                contentDescription = "Keepsake photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(0.9f)
                    .clip(RoundedCornerShape(2.dp))
            )
        }

        // Remove badge, top-right corner.
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(26.dp)
                .clickable(onClick = onRemove),
            shape = RoundedCornerShape(50),
            color = Terracotta
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove photo",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AddMoreTile(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(8.dp))
            .background(FadedTerracotta.copy(alpha = 0.35f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add more photos",
                tint = Terracotta,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Add more",
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                color = InkBrown
            )
        }
    }
}