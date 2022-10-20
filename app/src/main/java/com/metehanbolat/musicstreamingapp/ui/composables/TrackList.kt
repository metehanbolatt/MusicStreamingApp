package com.metehanbolat.musicstreamingapp.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.metehanbolat.musicstreamingapp.models.Track
import com.metehanbolat.musicstreamingapp.R

@Composable
fun TrackList(
    isPlaying: MutableState<Boolean>,
    listState: LazyListState,
    playingSongIndex: MutableState<Int>,
    overLayIcon: Int,
    tracks: List<Track>,
    onTrackItemClick: (Track) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(16.dp),
        state = listState
    ) {
        items(
            items = tracks,
            itemContent = {
                TrackListItem(
                    track = it,
                    playingSongIndex = playingSongIndex,
                    isPlaying = isPlaying,
                    overLayIcon = overLayIcon,
                    onTrackItemClick = onTrackItemClick
                )
            }
        )
    }
}

@Composable
fun TrackListItem(
    track: Track,
    playingSongIndex: MutableState<Int>,
    isPlaying: MutableState<Boolean>,
    overLayIcon: Int,
    onTrackItemClick: (Track) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onTrackItemClick(track) }
    ) {
        Column {
            Box(modifier = Modifier.padding(6.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(track.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = track.songTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .size(120.dp)
                )
                if (playingSongIndex.value == track.index && isPlaying.value) {
                    OverlayRoundedBox(
                        shape = RoundedCornerShape(32.dp),
                        color = Color.Gray.copy(alpha = 0.6f),
                        size = 120.dp,
                        overLayIcon = overLayIcon,
                        contentDescription = stringResource(id = R.string.play_indicator)
                    )
                }
            }
        }
    }
}

@Composable
fun OverlayRoundedBox(
    shape: RoundedCornerShape,
    color: Color,
    size: Dp,
    overLayIcon: Int,
    contentDescription: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(color)
                .size(size)
        ) {
            Image(
                painter = painterResource(id = overLayIcon),
                contentDescription = contentDescription,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
