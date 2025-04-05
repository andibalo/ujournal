package id.ac.umn.ujournal.ui.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UJournalBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier.fillMaxWidth(),
        sheetState = sheetState,
        onDismissRequest = onDismiss,
    ) {
        content()
    }
}

