package com.mort.przepisownia.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarView(
    title: String,
    onBackNavClick: () -> Unit = {},
    dropdownMenuItems: List<MenuDropdownItem> = emptyList(),
    searchable: Boolean = false,
    isSearching: Boolean = false,
    onSearchClick: () -> Unit = {},
    searchQuery: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearchFocusChanged: (Boolean) -> Unit = {}
) {
    val searchViewRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isSearching) {
        if (isSearching) {
            delay(100)
            searchViewRequester.requestFocus()
        }
    }

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
            if (isSearching) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(searchViewRequester)
                        .onFocusChanged { focusState ->
                            onSearchFocusChanged(focusState.isFocused)
                        },
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Szukaj przepisu...") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            } else {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
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
                        imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (isSearching) "Zamknij wyszukiwanie" else "Szukaj"
                    )
                }
            }
        }
    )
}