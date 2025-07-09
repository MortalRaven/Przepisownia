package com.mort.przepisownia.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarView(
    title: String,
    onBackNavClick: () -> Unit = {},
    dropdownMenuItems: List<MenuDropdownItem> = emptyList()
) {
    val navIcon: (@Composable () -> Unit) =
        {
            IconButton(onClick = { onBackNavClick() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        }

    TopAppBar(
        modifier = Modifier.shadow(elevation = 3.dp),
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 4.dp)
            )
        },
        navigationIcon = navIcon,
        actions = {
            if (dropdownMenuItems.isNotEmpty()) {
                MenuDropdown(menuItems = dropdownMenuItems)
            }
        }
    )
}