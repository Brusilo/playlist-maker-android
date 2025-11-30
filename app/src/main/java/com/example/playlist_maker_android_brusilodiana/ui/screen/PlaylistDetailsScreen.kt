package com.example.playlist_maker_android_brusilodiana.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.domain.models.Playlist
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import com.example.playlist_maker_android_brusilodiana.ui.component.TrackListItemNew
import com.example.playlist_maker_android_brusilodiana.ui.view_model.PlaylistViewModel
import com.example.playlist_maker_android_brusilodiana.creator.Creator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailsScreen(
    playlistId: Long,
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit = {},
    navController: NavController? = null
) {
    val context = LocalContext.current
    val playlistViewModel: PlaylistViewModel = viewModel(
        factory = Creator.getPlaylistViewModelFactory(context)
    )

    val playlist by playlistViewModel.getPlaylistById(playlistId).collectAsState(initial = null)
    var showOptionsSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(playlistId) {
        playlistViewModel.getPlaylistById(playlistId)
    }

    Scaffold(
        containerColor = colorResource(id = R.color.white),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.playlist_details_screen_title),
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
        }
    ) { innerPadding ->
        if (playlist != null) {
            PlaylistDetailsContent(
                playlist = playlist!!,
                onTrackClick = onTrackClick,
                onOptionsClick = { showOptionsSheet = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.loading_text),
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.gray)
                )
            }
        }

        if (showOptionsSheet && playlist != null) {
            ModalBottomSheet(
                onDismissRequest = { showOptionsSheet = false },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = playlist!!.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.black)
                    )
                    Text(
                        text = getTracksCountText(playlist!!.tracks.size),
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.gray),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = {
                            playlistViewModel.sharePlaylist(playlist!!)
                            showOptionsSheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.share_playlist),
                            color = colorResource(id = R.color.black),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Start
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            showOptionsSheet = false
                            showDeleteDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.delete_playlist),
                            color = colorResource(id = R.color.black),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Start
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(
                        text = stringResource(R.string.confirm_delete_playlist),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                text = {},
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text(
                                text = stringResource(R.string.no_button),
                                color = colorResource(id = R.color.black)
                            )
                        }
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                kotlinx.coroutines.MainScope().launch(Dispatchers.IO) {
                                    try {
                                        playlistViewModel.deletePlaylist(playlistId)
                                        navController?.popBackStack()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
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
    }
}

@Composable
fun PlaylistDetailsContent(
    playlist: Playlist,
    onTrackClick: (Track) -> Unit,
    onOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            PlaylistHeader(playlist = playlist, onOptionsClick = onOptionsClick)
        }

        items(playlist.tracks) { track ->
            TrackListItemNew(
                track = track,
                onTrackClick = { onTrackClick(track) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PlaylistHeader(playlist: Playlist, onOptionsClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = colorResource(id = R.color.white),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 2.dp,
                    color = colorResource(id = R.color.gray),
                    shape = RoundedCornerShape(12.dp)
                )
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.LibraryMusic,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = colorResource(id = R.color.gray)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = playlist.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.black)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (playlist.description.isNotEmpty()) {
            Text(
                text = playlist.description,
                fontSize = 16.sp,
                color = colorResource(id = R.color.gray)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalMinutes = calculateTotalMinutes(playlist.tracks)
            val tracksCount = playlist.tracks.size

            Text(
                text = stringResource(R.string.minutes_count, totalMinutes),
                fontSize = 14.sp,
                color = colorResource(id = R.color.gray)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.dot_separator),
                fontSize = 14.sp,
                color = colorResource(id = R.color.gray)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = getTracksCountText(tracksCount),
                fontSize = 14.sp,
                color = colorResource(id = R.color.gray)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        IconButton(
            onClick = onOptionsClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.options_button),
                tint = colorResource(id = R.color.gray)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun getTracksCountText(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> stringResource(R.string.tracks_count_single, count)
        count % 10 in 2..4 && count % 100 !in 12..14 -> stringResource(R.string.tracks_count_few, count)
        else -> stringResource(R.string.tracks_count_many, count)
    }
}

private fun calculateTotalMinutes(tracks: List<Track>): Int {
    var totalSeconds = 0
    tracks.forEach { track ->
        val timeParts = track.trackTime.split(":")
        if (timeParts.size == 2) {
            val minutes = timeParts[0].toIntOrNull() ?: 0
            val seconds = timeParts[1].toIntOrNull() ?: 0
            totalSeconds += minutes * 60 + seconds
        } else if (timeParts.size == 1) {
            val minutes = timeParts[0].toIntOrNull() ?: 0
            totalSeconds += minutes * 60
        }
    }
    return (totalSeconds / 60).coerceAtLeast(1)
}