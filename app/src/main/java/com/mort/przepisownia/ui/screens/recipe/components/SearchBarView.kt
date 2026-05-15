package com.mort.przepisownia.ui.screens.recipe.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mort.przepisownia.R
import kotlinx.coroutines.delay

@Composable
fun SearchBarView(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusChanged(it.isFocused) },
        value = searchQuery,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.search_for_recipe)) },
        singleLine = true
    )
}