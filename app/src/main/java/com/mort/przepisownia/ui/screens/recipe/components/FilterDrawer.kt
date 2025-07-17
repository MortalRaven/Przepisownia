package com.mort.przepisownia.ui.screens.recipe.components

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
import androidx.compose.ui.unit.dp

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
            text = "Sortuj według",
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
                    text = sortType.label
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
                text = "Pokaż ulubione"
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onApply
        ) {
            Text("Zastosuj")
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onReset
        ) {
            Text("Wyczyść filtry")
        }
    }
}

enum class SortType(val label: String) {
    ALPHABET_ASC("A-Z"),
    ALPHABET_DESC("Z-A"),
    DATE_ADDED_ASC("Data dodania: rosnąco"),
    DATE_ADDED_DESC("Data dodania: malejąco"),
    DATE_VIEWED_ASC("Ostatnio oglądane: rosnąco"),
    DATE_VIEWED_DESC("Ostatnio oglądane: malejąco")
}
