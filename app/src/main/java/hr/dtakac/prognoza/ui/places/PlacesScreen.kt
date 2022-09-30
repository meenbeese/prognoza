package hr.dtakac.prognoza.ui.places

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import hr.dtakac.prognoza.presentation.places.PlacesViewModel
import hr.dtakac.prognoza.ui.theme.PrognozaTheme
import hr.dtakac.prognoza.R
import hr.dtakac.prognoza.presentation.places.PlaceUi

@Composable
fun PlacesScreen(
    viewModel: PlacesViewModel = hiltViewModel(),
    onPlaceSelected: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified
) {
    // Get state on first start
    LaunchedEffect(viewModel) {
        viewModel.getSaved()
    }
    // Notify place selected to update forecast state
    val state by remember { viewModel.state }
    LaunchedEffect(state.selectedPlace) {
        if (state.selectedPlace != null) {
            onPlaceSelected()
        }
    }

    Column(modifier = Modifier.background(backgroundColor)) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            SearchBar(
                modifier = Modifier.padding(
                    top = 24.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
                onSubmit = viewModel::search,
                onQueryChange = { if (it.isBlank()) viewModel.getSaved() }
            )
            Crossfade(targetState = state.isLoading) { isLoading ->
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 24.dp),
                        color = LocalContentColor.current,
                        trackColor = backgroundColor
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 24.dp),
                        color = LocalContentColor.current,
                        trackColor = backgroundColor,
                        progress = 1f
                    )
                }
            }
            val empty = state.empty
            if (empty != null) {
                Text(
                    text = empty.asString(),
                    style = PrognozaTheme.typography.subtitleMedium,
                    color = LocalContentColor.current.copy(alpha = PrognozaTheme.alpha.medium),
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .weight(1f)
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(state.places) { idx, placeUi ->
                        if (idx == 0) Spacer(modifier = Modifier.height(12.dp))
                        PlaceItem(
                            placeUi = placeUi,
                            modifier = Modifier
                                .clickable(
                                    onClick = { viewModel.select(idx) },
                                    indication = rememberRipple(bounded = true),
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .fillMaxWidth()
                                .padding(
                                    vertical = 10.dp,
                                    horizontal = 24.dp
                                )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .clickable(
                        onClick = onSettingsClick,
                        indication = rememberRipple(bounded = true),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // todo: remove these comments at some point
                /*Image(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalContentColor.current.copy(alpha = PrognozaTheme.alpha.medium)),
                    modifier = Modifier.size(28.dp)
                )*/
                Text(
                    text = stringResource(id = R.string.settings),
                    style = PrognozaTheme.typography.titleMedium,
                    //modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    onSubmit: (String) -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val style = PrognozaTheme.typography.subtitleMedium.copy(color = LocalContentColor.current)
    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = query,
        onValueChange = {
            query = it
            onQueryChange(it)
        },
        maxLines = 1,
        textStyle = style,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions {
            onSubmit(query)
            focusManager.clearFocus()
        },
        cursorBrush = SolidColor(LocalContentColor.current),
        decorationBox = { innerTextField ->
            Column {
                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(LocalContentColor.current.copy(alpha = PrognozaTheme.alpha.medium)),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                stringResource(id = R.string.search_places),
                                style = style,
                                color = LocalContentColor.current.copy(alpha = PrognozaTheme.alpha.medium)
                            )
                        }
                        innerTextField()
                    }
                }
            }
        }
    )
}

@Composable
private fun PlaceItem(
    placeUi: PlaceUi,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (placeUi.isSelected) {
                Image(
                    painter = painterResource(id = R.drawable.ic_my_location),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(20.dp),
                    colorFilter = ColorFilter.tint(LocalContentColor.current.copy(alpha = PrognozaTheme.alpha.medium))
                )
            }
            Text(
                text = placeUi.name.asString(),
                style = PrognozaTheme.typography.subtitleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = placeUi.details.asString(),
            style = PrognozaTheme.typography.body,
            color = LocalContentColor.current.copy(alpha = PrognozaTheme.alpha.medium),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}