package com.metehanbolat.musicstreamingapp.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.metehanbolat.musicstreamingapp.R
import com.metehanbolat.musicstreamingapp.models.Track
import com.metehanbolat.musicstreamingapp.ui.theme.dialogColor

@Composable
fun TrackDetailsDialog(
    mutableTrack: MutableState<Track?>,
    openDialog: MutableState<Boolean>
) {
    if (openDialog.value && mutableTrack.value != null) {
        val track = mutableTrack.value!!
        AlertDialog(
            backgroundColor = dialogColor,
            title = {
                Text(text = track.songTitle)
            },
            text = {
                Column {
                    Text(text = stringResource(id = R.string.artist) + " " + track.artist)
                    Text(text = stringResource(id = R.string.trackNo) + " " + track.index + 1)
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        onClick = { openDialog.value = false}
                    ) {
                        Text(text = stringResource(id = R.string.dismiss))
                    }
                }
            },
            onDismissRequest = { openDialog.value = false }
        )
        
    }
}