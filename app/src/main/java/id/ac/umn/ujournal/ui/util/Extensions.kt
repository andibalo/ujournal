package id.ac.umn.ujournal.ui.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun Timestamp.toLocalDateTime(): LocalDateTime {
    return this.toDate().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

fun LocalDateTime.toDate(zone : ZoneId = ZoneId.systemDefault()): Date {
    return Date.from(this.atZone(zone).toInstant())
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

fun NavDestination?.hasRoute(destination: String): Boolean {
    return this?.route == destination
}

fun WindowSizeClass.isCompact() =
    windowWidthSizeClass == WindowWidthSizeClass.COMPACT ||
            windowHeightSizeClass == WindowHeightSizeClass.COMPACT

fun Uri.getFileExtension(context: Context): String? {
    val fileType: String? = context.contentResolver.getType(this)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
}

@Composable
fun Modifier.shimmerLoading(
    durationMillis: Int = 1000,
): Modifier {
    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )

    return drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.LightGray.copy(alpha = 0.7f),
                    Color.LightGray.copy(alpha = 1.0f),
                    Color.LightGray.copy(alpha = 0.7f),
                ),
                start = Offset(x = translateAnimation, y = translateAnimation),
                end = Offset(x = translateAnimation + 100f, y = translateAnimation + 100f),
            )
        )
    }
}