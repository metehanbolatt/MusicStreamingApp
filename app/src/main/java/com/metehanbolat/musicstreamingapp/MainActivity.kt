package com.metehanbolat.musicstreamingapp

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.metehanbolat.musicstreamingapp.models.MusicPlayerOption
import com.metehanbolat.musicstreamingapp.models.Track
import com.metehanbolat.musicstreamingapp.ui.composables.*
import com.metehanbolat.musicstreamingapp.ui.theme.MusicStreamingAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnMusicButtonClick {

    private val viewModel: TrackViewModel by viewModels()

    private val isPlaying = mutableStateOf(false)
    private var trackList = listOf<Track>()
    private lateinit var currentSong: MutableState<Track>
    private val clickedSong: MutableState<Track?> = mutableStateOf(null)
    private val currentSongIndex = mutableStateOf(-1)
    private val turnTableArmState = mutableStateOf(false)
    private val isBuffering = mutableStateOf(false)
    private val isTurnTableArmFinished = mutableStateOf(false)
    private lateinit var listState: LazyListState
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
        setContent {
            MusicStreamingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    listState = rememberLazyListState()
                    coroutineScope = rememberCoroutineScope()
                    val openDialog = remember { mutableStateOf(false) }
                    val trackList by viewModel.trackList.observeAsState()
                    if (trackList?.isNotEmpty() == true) {
                        MainContent(
                            currentSong = currentSong,
                            listState = listState,
                            currentSongIndex = currentSongIndex,
                            isTurnTableArmState = turnTableArmState,
                            isTurnTableArmFinished= isTurnTableArmFinished,
                            trackList = trackList!!,
                            onMusicButtonClick = this@MainActivity
                        ) { song ->
                            clickedSong.value = song
                            openDialog.value = true
                        }
                    } else {
                        LoadingScreen()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    @Composable
    private fun MainContent(
        currentSong: MutableState<Track>,
        listState: LazyListState,
        currentSongIndex: MutableState<Int>,
        isTurnTableArmState: MutableState<Boolean>,
        isTurnTableArmFinished: MutableState<Boolean>,
        trackList: List<Track>,
        onMusicButtonClick: OnMusicButtonClick,
        onTrackItemClick: (Track) -> Unit
    ) {
        Column {
            Title()
            TrackList(
                isPlaying = isPlaying,
                listState = listState,
                playingSongIndex = currentSongIndex,
                overLayIcon = R.drawable.ic_baseline_pause_24,
                tracks = trackList,
                onTrackItemClick = onTrackItemClick
            )
            TurnTable(
                isPlaying = isPlaying,
                turnTableArmState = isTurnTableArmState,
                isTurnTableArmFinished = isTurnTableArmFinished
            )
            Player(
                track = currentSong,
                isPlaying = isPlaying,
                onMusicPlayerClick = onMusicButtonClick,
                isTurnTableArmFinished = isTurnTableArmFinished,
                isBuffering = isBuffering
            )
        }
    }

    private fun observeViewModel() {
        viewModel.trackList.observe(this) { trackList ->
            if (trackList.isNotEmpty()) {
                this.trackList = trackList
                currentSong = mutableStateOf(trackList.first())
            }
        }
    }

    private fun play() {
        try {
            if (this::mediaPlayer.isInitialized && isPlaying.value) {
                mediaPlayer.stop()
                mediaPlayer.release()
                isPlaying.value = false
                turnTableArmState.value = false
                isTurnTableArmFinished.value = false
            }
            isBuffering.value = true
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(currentSong.value.trackUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                isBuffering.value = false
                isPlaying.value = true
                if (!turnTableArmState.value) turnTableArmState.value = true
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateList() {
        coroutineScope.launch {
            if (isPlaying.value) currentSong.value.isPlaying = true
            listState.animateScrollToItem(currentSong.value.index)
        }
    }

    override fun onMusicButtonClick(command: MusicPlayerOption) {
        when(command) {
            MusicPlayerOption.Play -> {
                currentSong.value.isPlaying = !isPlaying.value
                currentSongIndex.value = currentSong.value.index
                try {
                    if (this::mediaPlayer.isInitialized && isPlaying.value) {
                        mediaPlayer.stop()
                        mediaPlayer.release()
                        isPlaying.value = false
                    } else play()
                } catch (e: Exception) {
                    e.printStackTrace()
                    mediaPlayer.release()
                    isPlaying.value = false
                }
            }
            MusicPlayerOption.Skip -> {
                var nextSongIndex = currentSong.value.index + 1

                if (currentSong.value.index == trackList.size - 1) {
                    nextSongIndex = 0
                    if (isPlaying.value) currentSongIndex.value = 0
                } else currentSongIndex.value++

                currentSong.value = trackList[nextSongIndex]

                if (isPlaying.value) play()
                updateList()
            }
            MusicPlayerOption.Previous -> {
                var previousSongIndex = currentSong.value.index - 1

                if (currentSong.value.index == 0) {
                    previousSongIndex = trackList.lastIndex
                    if (isPlaying.value) currentSongIndex.value = trackList.lastIndex
                } else currentSongIndex.value--

                currentSong.value = trackList[previousSongIndex]

                if (isPlaying.value) play()
                updateList()

            }
        }
    }
}

interface OnMusicButtonClick {
    fun onMusicButtonClick(command: MusicPlayerOption )
}
