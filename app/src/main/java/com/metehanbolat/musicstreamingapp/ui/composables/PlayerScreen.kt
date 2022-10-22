package com.metehanbolat.musicstreamingapp.ui.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.metehanbolat.musicstreamingapp.OnMusicButtonClick
import com.metehanbolat.musicstreamingapp.R
import com.metehanbolat.musicstreamingapp.models.MusicPlayerOption
import com.metehanbolat.musicstreamingapp.models.Track
import com.metehanbolat.musicstreamingapp.ui.theme.titleFont

@Composable
fun Title() {
    Column(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.app_title),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = titleFont,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Player(
    track: MutableState<Track>,
    isPlaying: MutableState<Boolean>,
    onMusicPlayerClick: OnMusicButtonClick,
    isTurnTableArmFinished: MutableState<Boolean>,
    isBuffering: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = track.value.songTitle,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            val canvasHeight = remember { mutableStateOf(0f) }
            val musicBarAnim = rememberInfiniteTransition()
            val musicBarHeight by musicBarAnim.animateFloat(
                initialValue = 0f,
                targetValue = if (isTurnTableArmFinished.value && isPlaying.value) canvasHeight.value else 0f,
                animationSpec = infiniteRepeatable(
                    tween(500),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(180f)
                    .background(Color.DarkGray.copy(0.5f))
            ) {
                val canvasWidth = this.size.width
                canvasHeight.value = this.size.height

                for (i in 0..7) {
                    drawRect(
                        color = Color.DarkGray.copy(alpha = 0.8f),
                        size = Size(canvasWidth / 9, musicBarHeight),
                        topLeft = Offset(x = canvasWidth / 8 * i.toFloat(), y = 0f)
                    )
                }
            }

            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_skip_previous_24),
                    contentDescription = stringResource(id = R.string.previous),
                    modifier = Modifier
                        .clickable(!isBuffering.value, onClick = {
                            onMusicPlayerClick.onMusicButtonClick(MusicPlayerOption.Previous)
                        })
                        .padding(16.dp)
                        .size(35.dp)
                )
                Image(
                    painter = painterResource(
                        id = if (isPlaying.value) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24
                    ),
                    contentDescription = stringResource(id = R.string.play_indicator),
                    modifier = Modifier
                        .clickable(!isBuffering.value, onClick = {
                            onMusicPlayerClick.onMusicButtonClick(MusicPlayerOption.Previous)
                        })
                        .padding(16.dp)
                        .size(35.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_skip_next_24),
                    contentDescription = stringResource(id = R.string.next_song),
                    modifier = Modifier
                        .clickable(!isBuffering.value, onClick = {
                            onMusicPlayerClick.onMusicButtonClick(MusicPlayerOption.Skip)
                        })
                        .padding(16.dp)
                        .size(35.dp)
                )

                if (isBuffering.value) {
                    ProgressOverlay()
                }
            }
        }
    }
}

@Composable
fun TurnTable(
    isPlaying: MutableState<Boolean>,
    turnTableArmState: MutableState<Boolean>,
    isTurnTableArmFinished: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        TurnTableDrawable(
            shape = RoundedCornerShape(20.dp),
            size = 240.dp,
            turnTableDrawable = R.drawable.record,
            isPlaying = isPlaying,
            turnTableArmState,
            isTurnTableArmFinished
        )
    }
}

@Composable
fun TurnTableDrawable(
    shape: RoundedCornerShape,
    size: Dp,
    turnTableDrawable: Int,
    isPlaying: MutableState<Boolean>,
    turnTableArmState: MutableState<Boolean>,
    turnTableArmFinished: MutableState<Boolean>
) {
    val playingState = remember { isPlaying }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(MaterialTheme.colors.primary)
        ) {
            val infiniteTransition = rememberInfiniteTransition()

            val turnTableRotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    tween(5000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            val turnTableArmAim by animateFloatAsState(
                targetValue = if (turnTableArmState.value) 35f else 0f,
                animationSpec = tween(1000, easing = FastOutSlowInEasing),
                finishedListener = { turnTableArmFinished.value = true }
            )

            Image(
                painter = painterResource(id = turnTableDrawable),
                contentDescription = stringResource(R.string.turn_table),
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.Center)
                    .rotate(degrees = if (playingState.value && turnTableArmFinished.value) turnTableRotation else 0f)
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.DarkGray)
                    .clip(CircleShape)
                    .padding(12.dp)
                    .align(Alignment.BottomStart)
            ) {
                Column(
                    modifier = Modifier
                        .rotate(turnTableArmAim)
                        .align(Alignment.BottomStart)
                        .padding(12.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp, height = 120.dp)
                            .background(Color.LightGray)
                            .border(BorderStroke(2.dp, Color.DarkGray))
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressOverlay() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(Color.Gray.copy(alpha = 0.6f))
                .fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

