package id.ac.umn.ujournal.ui.components.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerDialogDefaults
import androidx.compose.material3.TimePickerDisplayMode
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    onDismiss: () -> Unit,
    onConfirm: (TimePickerState) -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    TimePickerDialog(
        title = { TimePickerDialogDefaults.Title(displayMode = TimePickerDisplayMode.Picker) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(timePickerState)
                }
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        TimePicker(state = timePickerState)
    }
}