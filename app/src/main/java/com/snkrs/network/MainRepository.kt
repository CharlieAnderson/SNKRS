package com.snkrs.network

import com.snkrs.network.models.Track
import com.snkrs.network.response.AuthTokenResponse
import com.snkrs.network.models.Artist
import com.snkrs.network.models.TrackAudioFeatures
import retrofit2.await

/**
 * Repository used by ViewModels for remote and local data retrieval.
 * Data is fetched remotely, though in some cases is stored in memory locally.
 *
 * All methods first check for an valid Auth Token from Spotify
 */
class MainRepository(private val remote: MainRemote) {
	companion object {
		const val MILLIS = 1000
		const val AUTHORIZATION_BEARER = "Authorization: Bearer"
	}
	private lateinit var token: String
	private var tokenExpiry: Long = 0
	private var artistTopTracks: List<Track>? = null
	private var audioFeatures: TrackAudioFeatures? = null

	/**
	 * Take a query for an artist name and search for an artist.
	 */
	suspend fun searchForArtist(query: String): Artist? {
		if (isTokenExpired()) getAuthToken()
		val artist = remote.searchArtists(token, query).await().artists.artist.firstOrNull()
		artistTopTracks = null
		audioFeatures = null
		return artist
	}

	/**
	 * Return a list of a particular artist's top tracks on Spotify.
	 */
	suspend fun getArtistTopTracks(artistId: String): List<Track> {
		if (isTokenExpired()) getAuthToken()
		return artistTopTracks ?: remote.getArtistTopTracks(token, artistId).await().tracks
	}

	/**
	 * Return an analysis of a particular Track's audio features
	 */
	suspend fun getTrackAudioFeatures(trackId: String): TrackAudioFeatures {
		if (isTokenExpired()) getAuthToken()
		return audioFeatures ?: remote.getTrackAudioFeatures(token, trackId).await()
	}

	/**
	 * refresh the authorization token from Spotify
	 */
	private suspend fun getAuthToken(): AuthTokenResponse {
		val response = remote.getAuthToken().await()
		token = "$AUTHORIZATION_BEARER ${response.token}"
		tokenExpiry = System.currentTimeMillis() + (response.expiresIn * MILLIS)
		return response
	}

	/**
	 * Check if authorization token is expired.
	 */
	private fun isTokenExpired() = (System.currentTimeMillis() > tokenExpiry)
}
