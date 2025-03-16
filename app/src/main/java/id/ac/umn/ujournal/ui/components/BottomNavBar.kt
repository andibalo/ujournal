package id.ac.umn.ujournal.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import id.ac.umn.ujournal.ui.navigation.bottomTabRowScreens
import id.ac.umn.ujournal.ui.navigation.navigateSingleTopTo

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    var selectedItem by rememberSaveable { mutableStateOf(0) }

    NavigationBar {
        bottomTabRowScreens.forEachIndexed { index, item ->
            NavigationBarItem(
                alwaysShowLabel = true,
                icon = { Icon(item.icon, contentDescription = item.route) },
                label = { Text(item.name) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigateSingleTopTo(item.route)
                }
            )
        }
    }
}