package com.snkrs.network

import com.snkrs.encodeToBase64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Remote class used directly for network API calls to the Spotify endpoints defined in [SpotifyApi]
 */

class MainRemote {
	companion object {
		private const val SPOTIFY_AUTH_URL = "https://accounts.spotify.com/"
		private const val SPOTIFY_BASE_URL = "https://api.spotify.com/"
		private const val SPOTIFY_CLIENT_ID = "20b7a926bd324636a1bb32771acf42ea"
		private const val SPOTIFY_CLIENT_SECRET = "85aff50cc117450ba2025a2c5afe755b"
		private const val SPOTIFY_CLIENT_COMBO = "$SPOTIFY_CLIENT_ID:$SPOTIFY_CLIENT_SECRET"
		private const val BASIC = "Basic"
		private val spotifyAuthorization: String
			get() = "$BASIC ${SPOTIFY_CLIENT_COMBO.encodeToBase64()}"
	}

	val spotifyAuthApi: SpotifyApi by lazy {
		Retrofit.Builder()
			.baseUrl(SPOTIFY_AUTH_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.client(OkHttpClient())
			.build()
			.create(SpotifyApi::class.java)
	}

	val spotifyBaseApi: SpotifyApi by lazy {
		Retrofit.Builder()
			.baseUrl(SPOTIFY_BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.client(OkHttpClient())
			.build()
			.create(SpotifyApi::class.java)
	}

	fun getAuthToken() = spotifyAuthApi.getAuthToken(
		authorization = spotifyAuthorization,
		grantType = "client_credentials"
	)

	fun searchArtists(token: String, query: String) =
		spotifyBaseApi.searchArtists(
			authorization = token,
			query = query
		)

	fun getArtistTopTracks(token: String, artistId: String) = spotifyBaseApi.getArtistTopTracks(
		authorization = token,
		artistId = artistId
	)

	fun getTrackAudioFeatures(token: String, trackId: String) =
		spotifyBaseApi.getTrackAudioFeatures(
			authorization = token,
			trackId = trackId
		)
}