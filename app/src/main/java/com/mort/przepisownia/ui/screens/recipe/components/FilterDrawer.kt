package com.mort.przepisownia.ui.screens.recipe.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mort.przepisownia.R

@Composable
fun FilterDrawer(
    currentSort: SortType,
    showOnlyFavorites: Boolean,
    onSortChange: (SortType) -> Unit,
    onToggleFavorites: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.sort_by),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        SortType.entries.forEach { sortType ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSortChange(sortType) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = sortType == currentSort,
                    onClick = { onSortChange(sortType) }
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(sortType.label)
                    )
            }
        }

        Row(
            modifier = Modifier.clickable { onToggleFavorites() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = showOnlyFavorites,
                onCheckedChange = { onToggleFavorites() }
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(R.string.show_favourites)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onApply
        ) {
            Text(stringResource(R.string.apply))
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onReset
        ) {
            Text(stringResource(R.string.clear_filters))
        }
    }
}

enum class SortType(@StringRes val label: Int) {
    ALPHABET_ASC(R.string.sort_alphabet_asc),
    ALPHABET_DESC(R.string.sort_alphabet_desc),
    DATE_ADDED_ASC(R.string.sort_date_added_asc),
    DATE_ADDED_DESC(R.string.sort_date_added_desc),
    DATE_VIEWED_ASC(R.string.sort_date_viewed_asc),
    DATE_VIEWED_DESC(R.string.sort_date_viewed_desc)
}
