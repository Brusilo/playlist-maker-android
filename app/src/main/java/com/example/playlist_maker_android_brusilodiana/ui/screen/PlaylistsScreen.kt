package com.example.playlist_maker_android_brusilodiana.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.ui.component.PlaylistListItem
import com.example.playlist_maker_android_brusilodiana.ui.view_model.PlaylistViewModel
import com.example.playlist_maker_android_brusilodiana.creator.Creator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    onBackClick: () -> Unit,
    onCreateNewPlaylist: () -> Unit,
    onPlaylistClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val playlistViewModel: PlaylistViewModel = viewModel(
        factory = Creator.getPlaylistViewModelFactory(context)
    )
    val playlists by playlistViewModel.playlists.collectAsState(emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
    var playlistToDelete by remember { mutableStateOf<com.example.playlist_maker_android_brusilodiana.domain.models.Playlist?>(null) }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colorResource(id = R.color.white),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.playlists_screen_title),
                        color = colorResource(id = R.color.black),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                            tint = colorResource(id = R.color.black)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.white)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onCreateNewPlaylist() },
                containerColor = colorResource(id = R.color.gray),
                contentColor = colorResource(id = R.color.white)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_playlist),
                    tint = colorResource(id = R.color.white)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.playlists_empty_message),
                        color = colorResource(id = R.color.gray),
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(playlists) { playlist ->
                        PlaylistListItem(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist.id) },
                            onLongClick = {
                                playlistToDelete = playlist
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog && playlistToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.confirm_delete_playlist),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {},
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            playlistToDelete = null
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.no_button),
                            color = colorResource(id = R.color.black)
                        )
                    }
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            playlistToDelete?.let { playlist ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        playlistViewModel.deletePlaylist(playlist.id)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        showError = true
                                    }
                                }
                            }
                            playlistToDelete = null
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.yes_button),
                            color = colorResource(id = R.color.black)
                        )
                    }
                }
            },
            dismissButton = {}
        )
    }

    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = {
                Text(
                    text = stringResource(R.string.error_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(stringResource(R.string.delete_playlist_error))
            },
            confirmButton = {
                TextButton(
                    onClick = { showError = false }
                ) {
                    Text(stringResource(R.string.ok_button))
                }
            }
        )
    }
}