package com.metehanbolat.musicstreamingapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metehanbolat.musicstreamingapp.models.Track
import com.metehanbolat.musicstreamingapp.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    trackRepository: TrackRepository
): ViewModel() {

    private val _trackList = MutableLiveData<List<Track>>()
    val trackList: LiveData<List<Track>> get() = _trackList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            trackRepository.getTracks().let {
                _trackList.postValue(it.sortedBy { it.index })
            }
        }
    }
}