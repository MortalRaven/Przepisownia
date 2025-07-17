package com.mort.przepisownia.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mort.przepisownia.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarView(
    title: String,
    onBackNavClick: () -> Unit = {},
    dropdownMenuItems: List<MenuDropdownItem> = emptyList(),
    searchable: Boolean = false,
    isSearching: Boolean = false,
    onSearchClick: () -> Unit = {},
    onFilterClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

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

            if (searchable) {
                IconButton(
                    onClick = {
                        onSearchClick()
                        if (!isSearching) {
                            focusManager.clearFocus()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Szukaj"
                    )
                }

                IconButton(
                    onClick = { onFilterClick() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_filter_list_24),
                        contentDescription = "Filtrowanie"
                    )
                }
            }
        }
    )
}