package com.metehanbolat.musicstreamingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.metehanbolat.musicstreamingapp.models.MusicPlayerOption

class MainActivity : ComponentActivity() {

    private val viewModel: TrackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.trackList.observe(this) { trackList ->

        }
    }
}

interface OnMusicButtonClick {
    fun onMusicButtonClick(command: MusicPlayerOption)
}
