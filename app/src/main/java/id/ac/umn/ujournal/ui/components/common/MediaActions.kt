package id.ac.umn.ujournal.ui.components.common

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MediaActions(
    modifier: Modifier = Modifier,
    onSuccessTakePicture: (Uri) -> Unit = {},
    onSuccessChooseFromGallery: (Uri?) -> Unit = {},
) {

    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            onSuccessChooseFromGallery(uri)
        }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ImagePicker(
            modifier = modifier.fillMaxWidth(),
            onSuccessTakePicture = onSuccessTakePicture
        ){
            Row(
                modifier = modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(Icons.Filled.CameraAlt, contentDescription = "Take picture with camera")
                Spacer(Modifier.padding(horizontal = 8.dp))
                Text("Take photo")
            }
        }
        Row (
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    imagePicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(Icons.Filled.PermMedia, contentDescription = "Choose from gallery")
            Spacer(Modifier.padding(horizontal = 8.dp))
            Text("Choose from gallery")
        }
    }
}