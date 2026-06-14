package com.mort.przepisownia.ui.screens.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceItem(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    description: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
    action: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 24.dp)
            .clickable { onClick() }
            .padding(start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            IconContainer(icon = icon)
        }
        Details(
            title = title,
            description = description
        )
        if (action != null) {
            ActionContainer(action = action)
        }
    }
}

@Composable
fun IconContainer(
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.padding(8.dp)
    ) {
        icon()
    }
}

@Composable
private fun ActionContainer(
    action: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.padding(8.dp),
    ) {
        action()
    }
}

@Composable
private fun RowScope.Details(
    title: @Composable () -> Unit,
    description: @Composable (() -> Unit)?
) {
    Column(modifier = Modifier.weight(1f)) {
        CompositionLocalProvider(LocalTextStyle.provides(MaterialTheme.typography.titleMedium)) {
            title()
        }
        if (description != null) {
            CompositionLocalProvider(LocalTextStyle.provides(MaterialTheme.typography.bodyMedium)) {
                description()
            }
        }
    }
}

@Composable
fun SwitchPreference(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    description: @Composable (() -> Unit)? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    PreferenceItem(
        modifier = modifier,
        title = title,
        description = description,
        onClick = { onCheckedChange(!checked) }
    ) {
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
