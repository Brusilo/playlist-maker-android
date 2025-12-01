package com.example.playlist_maker_android_brusilodiana.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.domain.states.SearchState
import com.example.playlist_maker_android_brusilodiana.ui.component.TrackListItemNew
import com.example.playlist_maker_android_brusilodiana.ui.view_model.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    viewModel: SearchViewModel,
    onTrackClick: (com.example.playlist_maker_android_brusilodiana.domain.models.Track) -> Unit
) {
    val screenState by viewModel.searchScreenState.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle(initialValue = emptyList())
    var query by remember { mutableStateOf("") }

    val interactionSource = remember { MutableInteractionSource() }
    val isTextFieldFocused by interactionSource.collectIsFocusedAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
    ) {
        TopAppBar(
            title = {
                Text(
                    stringResource(R.string.search_screen_title),
                    color = colorResource(id = R.color.black),
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = colorResource(id = R.color.black)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(id = R.color.white)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { newValue ->
                    query = newValue
                    if (newValue.isEmpty()) {
                        viewModel.clearSearch()
                    }
                },
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                interactionSource = interactionSource,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (query.isNotEmpty()) {
                            viewModel.search(query)
                            keyboardController?.hide()
                        }
                    }
                ),
                leadingIcon = {
                    IconButton(
                        onClick = {
                            if (query.isNotEmpty()) {
                                viewModel.search(query)
                                keyboardController?.hide()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.search_icon),
                            tint = colorResource(id = R.color.black)
                        )
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = {
                            query = ""
                            viewModel.clearSearch()
                            keyboardController?.hide()
                        }) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.clear_search),
                                tint = colorResource(id = R.color.black)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorResource(id = R.color.black),
                    unfocusedTextColor = colorResource(id = R.color.black),
                    focusedPlaceholderColor = colorResource(id = R.color.gray),
                    unfocusedPlaceholderColor = colorResource(id = R.color.gray),
                    focusedContainerColor = colorResource(R.color.search_field_bg),
                    unfocusedContainerColor = colorResource(R.color.search_field_bg),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            if (isTextFieldFocused && searchHistory.isNotEmpty() && query.isEmpty() && screenState is SearchState.Initial) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colorResource(R.color.search_field_bg),
                            shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                        )
                ) {
                    Column {
                        searchHistory.forEach { historyItem ->
                            SearchHistoryItem(
                                query = historyItem,
                                onClick = {
                                    query = historyItem
                                    viewModel.search(historyItem)
                                    keyboardController?.hide()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (screenState) {
                is SearchState.Initial -> {
                }

                is SearchState.Searching -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is SearchState.Success -> {
                    val tracks = (screenState as SearchState.Success).foundList
                    if (tracks.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(tracks.size) { index ->
                                TrackListItemNew(
                                    track = tracks[index],
                                    onTrackClick = { onTrackClick(tracks[index]) }
                                )
                            }
                        }
                    } else {
                        EmptyResultScreen()
                    }
                }

                is SearchState.EmptyResult -> {
                    EmptyResultScreen()
                }

                is SearchState.Fail -> {
                    ErrorScreen(
                        errorMessage = (screenState as SearchState.Fail).error,
                        onRetryClick = { viewModel.refresh() }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchHistoryItem(
    query: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.History,
            contentDescription = null,
            tint = colorResource(id = R.color.gray),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = query,
            fontSize = 16.sp,
            color = colorResource(id = R.color.black),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EmptyResultScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img1),
                contentDescription = stringResource(R.string.no_results_image_desc),
                modifier = Modifier.size(120.dp)
            )
            Text(
                stringResource(R.string.no_results),
                color = colorResource(id = R.color.black),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.no_results_subtitle),
                color = colorResource(id = R.color.gray),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    errorMessage: String,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img2),
                contentDescription = stringResource(R.string.error_image_desc),
                modifier = Modifier.size(120.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    stringResource(R.string.search_error_title),
                    color = colorResource(id = R.color.black),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    errorMessage,
                    color = colorResource(id = R.color.gray),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.blue_background),
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.retry_button))
            }
        }
    }
}