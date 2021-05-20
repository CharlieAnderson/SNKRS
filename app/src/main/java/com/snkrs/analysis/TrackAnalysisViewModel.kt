package com.snkrs.analysis

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.RadarEntry
import com.snkrs.BaseViewModel
import com.snkrs.network.MainRepository
import com.snkrs.network.models.Track
import com.snkrs.network.models.TrackAudioFeatures
import com.snkrs.toBitmap
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class TrackAnalysisViewModel(private val repository: MainRepository): BaseViewModel() {
	companion object {
		const val MED_IMAGE_INDEX = 1
		const val FEATURE_MULTIPLIER = 100
		const val LOUDNESS_MULTIPLIER = 10
	}
	private var _selectedTrackData = MutableLiveData<Track>()
	val selectedTrackData: LiveData<Track> = _selectedTrackData

	private var _radarDataEntries = MutableLiveData<List<RadarEntry>>()
	val radarDataEntries: LiveData<List<RadarEntry>> = _radarDataEntries

	fun getSelectedTrackAnalysis(artistId: String, trackIndex: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			val track = repository.getArtistTopTracks(artistId)[trackIndex]
			_selectedTrackData.postValue(track)
			_radarDataEntries.postValue(getRadarEntries(repository.getTrackAudioFeatures(track.id)))
		}
	}

	fun getImageBitmapAsync() : Deferred<Bitmap?> {
		return viewModelScope.async(Dispatchers.IO) {
			_selectedTrackData.value?.album?.images?.get(MED_IMAGE_INDEX)?.url?.toBitmap()
		}
	}

	fun getRadarEntries(trackAnalysis: TrackAudioFeatures) = listOf(
		RadarEntry(trackAnalysis.danceability.toFloat() * FEATURE_MULTIPLIER),
		RadarEntry(trackAnalysis.energy.toFloat() * FEATURE_MULTIPLIER),
		RadarEntry(trackAnalysis.valence.toFloat() * FEATURE_MULTIPLIER),
		RadarEntry(trackAnalysis.acousticness.toFloat() * FEATURE_MULTIPLIER),
		RadarEntry(trackAnalysis.liveness.toFloat() * FEATURE_MULTIPLIER),
		RadarEntry(trackAnalysis.loudness.toFloat().absoluteValue * LOUDNESS_MULTIPLIER),
		RadarEntry(trackAnalysis.instrumentalness.toFloat() * FEATURE_MULTIPLIER)
	)
}