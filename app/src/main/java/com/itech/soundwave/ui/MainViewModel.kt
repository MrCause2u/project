package com.itech.soundwave.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.itech.soundwave.data.MediaStoreHelper
import com.itech.soundwave.model.AudioItem
import com.itech.soundwave.service.PlayerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val mediaStoreHelper = MediaStoreHelper(application)
    val playerController = PlayerController(application)

    private val _audioList = MutableStateFlow<List<AudioItem>>(emptyList())
    val audioList: StateFlow<List<AudioItem>> = _audioList

    fun loadAudioFiles() {
        viewModelScope.launch {
            _audioList.value = mediaStoreHelper.getAudioFiles()
        }
    }

    fun playAudio(item: AudioItem) {
        val list = _audioList.value
        val index = list.indexOf(item)
        if (index != -1) {
            playerController.setPlaylist(list, index)
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerController.release()
    }
}
