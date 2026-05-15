package com.mort.przepisownia.ui.common

import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
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
    onFilterClick: () -> Unit = {},
    layoutEditable: Boolean = false,
    onLayoutClick: () -> Unit = {},
    layoutType: ViewType? = null,
    acceptable: Boolean = false,
    onAcceptClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    val navIcon: (@Composable () -> Unit) =
        {
            IconButton(onClick = { onBackNavClick() }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_back_24),
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
                        painter = painterResource(R.drawable.baseline_search_24),
                        contentDescription = stringResource(R.string.search)
                    )
                }

                IconButton(
                    onClick = { onFilterClick() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_filter_list_24),
                        contentDescription = stringResource(R.string.filter)
                    )
                }
            }

            if (layoutEditable) {
                IconButton(
                    onClick = { onLayoutClick() }
                ) {
                    if (layoutType == ViewType.GRID) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_grid_view_24),
                            contentDescription = stringResource(R.string.view_grid)
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.baseline_view_list_24),
                            contentDescription = stringResource(R.string.view_list)
                        )
                    }
                }
            }

            if (acceptable) {
                IconButton(
                    onClick = { onAcceptClick() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_done_24),
                        contentDescription = stringResource(R.string.accept)
                    )
                }
            }
        }
    )
}