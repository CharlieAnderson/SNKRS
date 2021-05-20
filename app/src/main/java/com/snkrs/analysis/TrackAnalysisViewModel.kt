package com.snkrs.analysis

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.snkrs.BaseViewModel
import com.snkrs.network.MainRepository
import com.snkrs.network.models.Track
import com.snkrs.network.models.TrackAudioFeatures
import com.snkrs.toBitmap
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TrackAnalysisViewModel(private val repository: MainRepository): BaseViewModel() {
	companion object {
		const val mediumImageIndex = 1
	}
	private var _selectedTrackData = MutableLiveData<Track>()
	val selectedTrackData: LiveData<Track> = _selectedTrackData

	private var _trackAnalysisData = MutableLiveData<TrackAudioFeatures>()
	val trackAnalysisData: LiveData<TrackAudioFeatures> = _trackAnalysisData

	fun getSelectedTrackAnalysis(artistId: String, trackIndex: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			val track = repository.getArtistTopTracks(artistId)[trackIndex]
			_selectedTrackData.postValue(track)
			_trackAnalysisData.postValue(repository.getTrackAudioFeatures(track.id))
		}
	}

	fun getImageBitmapAsync() : Deferred<Bitmap?> {
		return viewModelScope.async(Dispatchers.IO) {
			_selectedTrackData.value?.album?.images?.get(mediumImageIndex)?.url?.toBitmap()
		}
	}
}