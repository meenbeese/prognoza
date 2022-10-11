package hr.dtakac.prognoza.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.dtakac.prognoza.R
import hr.dtakac.prognoza.presentation.TextResource
import hr.dtakac.prognoza.presentation.asString
import hr.dtakac.prognoza.presentation.settings.MultipleChoiceSetting
import hr.dtakac.prognoza.ui.theme.PrognozaTheme

@Composable
fun MultipleChoiceSettingItem(
    state: MultipleChoiceSetting,
    onPick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) = MultipleChoiceSettingItem(
    name = state.name.asString(),
    selectedValue = state.value.asString(),
    values = state.values.map { it.asString() },
    onPick = onPick,
    modifier = modifier
)

@Composable
private fun MultipleChoiceSettingItem(
    name: String,
    selectedValue: String,
    values: List<String>,
    onPick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var openDialog by remember { mutableStateOf(false) }
    ItemContent(
        name = name,
        value = selectedValue,
        onClick = { openDialog = true },
        modifier = modifier
    )
    if (openDialog) {
        ItemDialog(
            title = name,
            selectedOption = selectedValue,
            options = values,
            onConfirm = onPick,
            onDismiss = { openDialog = false }
        )
    }
}

@Composable
private fun ItemContent(
    name: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                indication = rememberRipple(bounded = true),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth()
    ) {
        Text(text = name, style = PrognozaTheme.typography.subtitleMedium)
        Text(
            text = value,
            style = PrognozaTheme.typography.body,
            color = LocalContentColor.current.copy(alpha = PrognozaTheme.alpha.medium)
        )
    }
}

@Composable
private fun ItemDialog(
    title: String,
    selectedOption: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onConfirm: (Int) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var selectedIndex by remember { mutableStateOf(options.indexOf(selectedOption)) }
    AlertDialog(
        modifier = modifier,
        containerColor = PrognozaTheme.colors.surface3,
        titleContentColor = PrognozaTheme.colors.onSurface,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = PrognozaTheme.typography.titleMedium
            )
        },
        text = {
            DialogOptions(
                options = options,
                onIndexSelect = { selectedIndex = it },
                selectedIndex = selectedIndex
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedIndex)
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.confirm),
                    style = PrognozaTheme.typography.titleSmall,
                    color = PrognozaTheme.colors.onSurface
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = PrognozaTheme.colors.onSurface,
                    style = PrognozaTheme.typography.titleSmall
                )
            }
        }
    )
}

@Composable
private fun DialogOptions(
    options: List<String>,
    onIndexSelect: (Int) -> Unit,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalContentColor provides PrognozaTheme.colors.onSurface) {
        LazyColumn(modifier = modifier) {
            itemsIndexed(options) { idx, option ->
                Row(
                    modifier = Modifier
                        .clickable(
                            onClick = { onIndexSelect(idx) },
                            indication = rememberRipple(bounded = true),
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = idx == selectedIndex,
                        onClick = null,
                        interactionSource = remember { MutableInteractionSource() },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = LocalContentColor.current.copy(alpha = PrognozaTheme.alpha.medium),
                            unselectedColor = LocalContentColor.current.copy(alpha = PrognozaTheme.alpha.medium),
                            disabledSelectedColor = LocalContentColor.current.copy(alpha = PrognozaTheme.alpha.disabled),
                            disabledUnselectedColor = LocalContentColor.current.copy(alpha = PrognozaTheme.alpha.disabled)
                        )
                    )
                    Text(
                        text = option,
                        style = PrognozaTheme.typography.subtitleMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ContentPreview() = PrognozaTheme {
    ItemContent(
        name = fakeState.name.asString(),
        value = fakeState.value.asString()
    )
}

@Preview
@Composable
private fun PickerDialogPreview() = PrognozaTheme {
    ItemDialog(
        title = fakeState.name.asString(),
        selectedOption = fakeState.value.asString(),
        options = fakeState.values.map { it.asString() }
    )
}

private val fakeState: MultipleChoiceSetting = MultipleChoiceSetting(
    name = TextResource.fromText("Temperature unit"),
    value = TextResource.fromText("Celsius"),
    values = listOf(
        TextResource.fromText("Celsius"),
        TextResource.fromText("Fahrenheit")
    )
)