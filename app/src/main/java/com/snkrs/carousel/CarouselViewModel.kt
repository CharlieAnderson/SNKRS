package com.snkrs.carousel

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.snkrs.BaseViewModel
import com.snkrs.analysis.TrackAnalysisViewModel.Companion.MED_IMAGE_INDEX
import com.snkrs.network.MainRepository
import com.snkrs.network.models.Track
import com.snkrs.network.models.Artist
import com.snkrs.toBitmap
import kotlinx.coroutines.*

/**
 * ViewModel for the [CarouselFragment]
 */
class CarouselViewModel(
	private val repository: MainRepository
): BaseViewModel() {
	companion object {
		const val TRACK_INDEX_KEY = "trackIndex"
		const val TRACK_ID_KEY = "trackId"
		const val ARTIST_ID_KEY = "artistId"
		const val TITLE_TEXT_EXTRA = "title_text"
		const val TRACK_CARD_EXTRA = "track_card"
		const val ALBUM_IMAGE_EXTRA = "album_image"
	}
	private var _artistData = MutableLiveData<Artist>()
	val artistData: LiveData<Artist> = _artistData

	private var _topTracksData = MutableLiveData<List<Track>>()
	val topTracksData: LiveData<List<Track>> = _topTracksData

	/**
	 * Uses the search bar query to search for an artist on spotify,
	 *  and populate the data for the artist and their top tracks.
	 */
	fun getArtistAndTrackData(query: String) {
		if (query.isBlank()) return
		viewModelScope.launch {
			repository.searchForArtist(query)?.let {
				_artistData.postValue(it)
				val topTracks = repository.getArtistTopTracks(artistId = it.id)
				_topTracksData.postValue(topTracks)
			}
		}
	}

	/**
	 * Get bitmaps from URL asynchronously.
	 */
	fun getImageBitmapsAsync(): Deferred<List<Bitmap>?> {
		return viewModelScope.async(Dispatchers.IO) {
			topTracksData.value?.mapNotNull {
				it.album.images[MED_IMAGE_INDEX].url.toBitmap()
			}?.toList()
		}
	}

	/**
	 * Converts Drawable to Bitmap
	 */
	fun getDrawableBitMap(resources: Resources, drawableId: Int): Deferred<Bitmap> {
		return viewModelScope.async(Dispatchers.IO) {
			BitmapFactory.decodeResource(resources, drawableId)
		}
	}

	/**
	 * Gets the list of names for the top tracks to display.
	 */
	fun getTopTrackNames(): List<String>? = topTracksData.value?.map { it.name }?.toList()

	/**
	 * Gets a bundle of the selected track information to pass to the Analysis fragment.
	 */
	fun getAnalysisBundle(trackText: String): Bundle {
		val trackIndex = _topTracksData.value?.indexOfFirst { trackText.contains(it.name) }
		val trackId = trackIndex?.let { _topTracksData.value?.get(trackIndex)?.id }
		val artistId = artistData.value?.id
		return bundleOf(
			TRACK_INDEX_KEY to trackIndex,
			TRACK_ID_KEY to trackId,
			ARTIST_ID_KEY to artistId
		)
	}
}