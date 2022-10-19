package com.metehanbolat.musicstreamingapp.models

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.metehanbolat.musicstreamingapp.di.Constants.ARTIST
import com.metehanbolat.musicstreamingapp.di.Constants.FILE_NAME
import com.metehanbolat.musicstreamingapp.di.Constants.NAME

data class Track(
    val image: String,
    val index: Int,
    val songTitle: String,
    val artist: String,
    val trackUrl: String,
    var isPlaying: Boolean,
    var fileName: String
)

fun QueryDocumentSnapshot.toTrack(index: Int, imageUrl: String, trackUrl: String): Track {
    return Track(
        image = imageUrl,
        songTitle = this.getString(NAME) ?: "",
        artist = this.getString(ARTIST) ?: "",
        fileName = this.getString(FILE_NAME) ?: "",
        isPlaying = false,
        index = index,
        trackUrl = trackUrl
    )
}
