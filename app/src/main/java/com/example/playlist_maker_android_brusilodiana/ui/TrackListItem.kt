package com.example.playlist_maker_android_brusilodiana.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import androidx.compose.foundation.clickable

@Composable
fun TrackListItemNew(
    track: Track,
    onTrackClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrackClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_music),
                contentDescription = stringResource(R.string.track_image_description, track.trackName),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(11.dp))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = track.trackName,
                    fontSize = 17.sp,
                    color = Color.Black
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = track.artistName,
                        fontSize = 14.sp,
                        color = colorResource(R.color.chevron_grey)
                    )
                    Text(
                        stringResource(R.string.dot_separator),
                        color = colorResource(R.color.chevron_grey)
                    )
                    Text(
                        text = track.trackTime,
                        fontSize = 14.sp,
                        color = colorResource(R.color.chevron_grey)
                    )
                }
            }
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = stringResource(R.string.arrow),
            tint = colorResource(R.color.chevron_grey),
            modifier = Modifier.size(20.dp)
        )
    }
}