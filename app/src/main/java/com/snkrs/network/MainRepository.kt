package com.snkrs.network

import com.snkrs.network.models.Track
import com.snkrs.network.response.AuthTokenResponse
import com.snkrs.network.models.Artist
import com.snkrs.network.response.TrackAudioFeaturesResponse
import retrofit2.await

class MainRepository(private val remote: MainRemote) {
	companion object {
		const val MILLIS = 1000
		const val AUTHORIZATION_BEARER = "Authorization: Bearer"
	}
	private lateinit var token: String
	private var tokenExpiry: Long = 0
	private lateinit var artist: Artist
	private var artistTopTracks: List<Track>? = null
	private var audioFeatures: TrackAudioFeaturesResponse? = null

	suspend fun searchForArtist(query: String): Artist {
		if (isTokenExpired()) getAuthToken()
		artist = remote.searchArtists(token, query).await().artists.artist.first()
		artistTopTracks = null
		audioFeatures = null
		return artist
	}

	suspend fun getArtistTopTracks(artistId: String): List<Track> {
		if (isTokenExpired()) getAuthToken()
		return artistTopTracks ?: remote.getArtistTopTracks(token, artistId).await().tracks
	}

	suspend fun getTrackAudioFeatures(trackId: String): TrackAudioFeaturesResponse {
		if (isTokenExpired()) getAuthToken()
		return audioFeatures ?: remote.getTrackAudioFeatures(token, trackId).await()
	}

	private suspend fun getAuthToken(): AuthTokenResponse {
		val response = remote.getAuthToken().await()
		token = "$AUTHORIZATION_BEARER ${response.token}"
		tokenExpiry = System.currentTimeMillis() + (response.expiresIn * MILLIS)
		return response
	}

	private fun isTokenExpired() = (System.currentTimeMillis() > tokenExpiry)
}
