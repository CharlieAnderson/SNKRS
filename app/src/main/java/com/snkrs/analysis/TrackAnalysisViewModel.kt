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
import kotlinx.coroutines.*
import kotlin.math.absoluteValue

/**
 *  ViewModel for the [TrackAnalysisFragment].
 */
class TrackAnalysisViewModel(
	val dispatcher: CoroutineDispatcher,
	val repository: MainRepository
): BaseViewModel(dispatcher) {
	companion object {
		const val MED_IMAGE_INDEX = 1
		const val FEATURE_MULTIPLIER = 100
	}
	private var _selectedTrackData = MutableLiveData<Track>()
	val selectedTrackData: LiveData<Track> = _selectedTrackData

	private var _radarDataEntries = MutableLiveData<List<RadarEntry>>()
	val radarDataEntries: LiveData<List<RadarEntry>> = _radarDataEntries

	/**
	 * gets the audio features for the track selected,
	 *  based on the artist id and index of the track within the top tracks list.
	 */
	fun getSelectedTrackAnalysis(artistId: String, trackIndex: Int) {
		viewModelScope.launch(dispatcher) {
			val track = repository.getArtistTopTracks(artistId)[trackIndex]
			_selectedTrackData.postValue(track)
			_radarDataEntries.postValue(getRadarEntries(repository.getTrackAudioFeatures(track.id)))
		}
	}

	/**
	 * gets the album cover bitmap from the selected track data.
	 */
	fun getImageBitmapAsync() : Deferred<Bitmap?> {
		return viewModelScope.async(dispatcher) {
			_selectedTrackData.value?.album?.images?.get(MED_IMAGE_INDEX)?.url?.toBitmap()
		}
	}

	/**
	 * returns a list of the pre-determined audio features of the track,
	 * with values adjusted to be between 0-100.
	 */
	fun getRadarEntries(trackAnalysis: TrackAudioFeatures) = listOf(
		RadarEntry(trackAnalysis.instrumentalness.toFloat() * FEATURE_MULTIPLIER),
		RadarEntry(trackAnalysis.energy.toFloat() * FEATURE_MULTIPLIER),
		RadarEntry(trackAnalysis.valence.toFloat() * FEATURE_MULTIPLIER),
		RadarEntry(trackAnalysis.acousticness.toFloat() * FEATURE_MULTIPLIER),
		RadarEntry(trackAnalysis.liveness.toFloat() * FEATURE_MULTIPLIER),
		RadarEntry(trackAnalysis.danceability.toFloat() * FEATURE_MULTIPLIER)
	)
}
