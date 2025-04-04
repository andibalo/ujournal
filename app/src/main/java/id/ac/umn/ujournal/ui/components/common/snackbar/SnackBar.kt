package id.ac.umn.ujournal.ui.components.common.snackbar

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import id.ac.umn.ujournal.ui.theme.extendedColor

enum class Severity{
    SUCCESS,
    INFO,
    ERROR,
    WARNING
}

data class UJournalSnackBarVisuals(
    override val message: String,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val severity: Severity = Severity.SUCCESS,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false
) : SnackbarVisuals

@Composable
fun UJournalSnackBar(
    modifier: Modifier = Modifier,
    snackbarData: SnackbarData,
    severity: Severity = Severity.SUCCESS,
    actionOnNewLine: Boolean = false,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: Color = MaterialTheme.extendedColor.success.colorContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    actionColor: Color = SnackbarDefaults.actionColor,
    actionContentColor: Color = SnackbarDefaults.actionContentColor,
    dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
) {

    var _containerColor = containerColor
    var _contentColor = contentColor

    if(severity == Severity.ERROR){
        _containerColor = MaterialTheme.colorScheme.error
        _contentColor = MaterialTheme.colorScheme.onError
    }

    if(severity == Severity.WARNING){
        _containerColor = MaterialTheme.extendedColor.warning.colorContainer
        _contentColor = MaterialTheme.colorScheme.onPrimary
    }

    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        containerColor = _containerColor,
        contentColor = _contentColor,
        actionColor = actionColor,
        actionContentColor = actionContentColor,
        dismissActionContentColor = dismissActionContentColor
    )
}

suspend fun showSnackbarWithVisuals(
    snackbarHostState : SnackbarHostState,
    message: String,
    severity: Severity = Severity.SUCCESS,
    duration: SnackbarDuration = SnackbarDuration.Short,
    actionLabel: String? = null,
    withDismissAction: Boolean = false
) : SnackbarResult {
    return snackbarHostState.showSnackbar(
        visuals = UJournalSnackBarVisuals(
            message = message,
            severity = severity,
            duration = duration,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction
        )
    )
}