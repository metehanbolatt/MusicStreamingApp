package com.metehanbolat.musicstreamingapp.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.metehanbolat.musicstreamingapp.di.Constants.ALBUM_ART
import com.metehanbolat.musicstreamingapp.di.Constants.ALBUM_ART_ALL_CAPS
import com.metehanbolat.musicstreamingapp.di.Constants.FILE_NAME
import com.metehanbolat.musicstreamingapp.di.Constants.TRACKS
import com.metehanbolat.musicstreamingapp.models.Track
import com.metehanbolat.musicstreamingapp.models.toTrack
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TrackRepository {
    private val storage = Firebase.storage
    private val trackRef = storage.reference
    private val albumArt = storage.reference.child(ALBUM_ART_ALL_CAPS)

    suspend fun getTracks() = suspendCoroutine<List<Track>> { result ->
        val trackList = mutableListOf<Track>()
        try {
            Firebase.firestore.collection(TRACKS).get()
                .addOnCompleteListener { task ->
                    var index = 0
                    task.result.forEach { document ->
                        val imageUrl = albumArt.child(document.getString(ALBUM_ART)!!)
                        val trackUrl = trackRef.child(document.getString(FILE_NAME)!!)
                        imageUrl.downloadUrl.addOnSuccessListener { imageDownloadUrl ->
                            trackUrl.downloadUrl.addOnSuccessListener { trackDownloadUrl ->
                                trackList.add(
                                    document.toTrack(
                                        index = index,
                                        imageUrl = imageDownloadUrl.toString(),
                                        trackUrl = trackDownloadUrl.toString()
                                    )
                                )
                                if (index == task.result.size() - 1) result.resume(trackList)
                                index++
                            }
                        }
                    }

                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}