package com.example.playlist_maker_android_brusilodiana.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import com.example.playlist_maker_android_brusilodiana.ui.view_model.PlaylistViewModel
import com.example.playlist_maker_android_brusilodiana.creator.Creator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    track: Track, // ИЗМЕНЕНО: принимаем объект Track, а не trackId
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val playlistViewModel: PlaylistViewModel = viewModel(
        factory = Creator.getPlaylistViewModelFactory(context)
    )

    var showPlaylistSheet by remember { mutableStateOf(false) }

    val favoriteTracks by playlistViewModel.favoriteTracks.collectAsState(emptyList())

    val isFavorite = remember(track, favoriteTracks) {
        favoriteTracks.any { favoriteTrack ->
            favoriteTrack.trackName == track.trackName &&
                    favoriteTrack.artistName == track.artistName
        }
    }

    Scaffold(
        containerColor = colorResource(id = R.color.white),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.track_details_screen_title),
                        color = colorResource(id = R.color.black),
                        fontSize = 20.sp
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorResource(id = R.color.search_field_bg))
                    ) {
                        if (track.artworkUrl.isNotEmpty() && track.artworkUrl != "null") {
                            AsyncImage(
                                model = track.artworkUrl,
                                contentDescription = track.trackName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_music),
                                contentDescription = track.trackName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = track.trackName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.black)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = track.artistName,
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.gray)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(colorResource(id = R.color.gray))
                                .clickable { showPlaylistSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.add_to_playlist),
                                tint = colorResource(id = R.color.white),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(colorResource(id = R.color.gray))
                                .clickable {
                                    MainScope().launch(Dispatchers.IO) {
                                        playlistViewModel.toggleFavorite(track, !isFavorite)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite)
                                    stringResource(R.string.remove_from_favorites)
                                else
                                    stringResource(R.string.add_to_favorites),
                                tint = if (isFavorite) colorResource(id = R.color.red) else colorResource(id = R.color.white),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.duration),
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.gray)
                        )

                        Text(
                            text = track.trackTime,
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.black),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (showPlaylistSheet) {
            TrackPlaylistSelectionBottomSheet(
                onDismissRequest = { showPlaylistSheet = false },
                onPlaylistSelected = { playlistId ->
                    MainScope().launch(Dispatchers.IO) {
                        playlistViewModel.insertTrackToPlaylist(track, playlistId)
                    }
                    showPlaylistSheet = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackPlaylistSelectionBottomSheet(
    onDismissRequest: () -> Unit,
    onPlaylistSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val playlistViewModel: PlaylistViewModel = viewModel(
        factory = Creator.getPlaylistViewModelFactory(context)
    )
    val playlists by playlistViewModel.playlists.collectAsState(emptyList())
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = colorResource(id = R.color.white)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.select_playlist),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.black)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (playlists.isEmpty()) {
                Text(
                    text = stringResource(R.string.playlists_empty_message),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.gray)
                )
            } else {
                playlists.forEach { playlist ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onPlaylistSelected(playlist.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = colorResource(id = R.color.white),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = colorResource(id = R.color.gray),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LibraryMusic,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = colorResource(id = R.color.gray)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = playlist.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorResource(id = R.color.black)
                                )
                                Text(
                                    text = playlist.description,
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.gray)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}