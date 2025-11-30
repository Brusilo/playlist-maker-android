package com.example.playlist_maker_android_brusilodiana.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.domain.models.Playlist

@Composable
fun PlaylistListItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Filled.LibraryMusic,
            contentDescription = playlist.name,
            modifier = Modifier.size(48.dp),
            tint = Color.Gray
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = playlist.name,
                fontSize = 16.sp
            )
            val tracksCount = playlist.tracks.size
            val tracksSuffix = when {
                tracksCount == 1 -> stringResource(R.string.tracks_suffix_single)
                tracksCount in 2..4 -> stringResource(R.string.tracks_suffix_few)
                else -> stringResource(R.string.tracks_suffix_many)
            }
            val tracksText = stringResource(
                R.string.tracks_count,
                tracksCount,
                tracksSuffix
            )
            Text(
                text = tracksText,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}