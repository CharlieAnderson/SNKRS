package com.snkrs.network

import com.snkrs.network.response.ArtistTopTracksResponse
import com.snkrs.network.response.AuthTokenResponse
import com.snkrs.network.response.SearchArtistsResponse
import com.snkrs.network.models.TrackAudioFeatures
import retrofit2.Call
import retrofit2.http.*

interface SpotifyApi {
	companion object {
		const val ARTIST = "artist"
		const val LIMIT_ONE = 1
		const val US_COUNTRY_CODE = "US"
	}

	@FormUrlEncoded
	@POST("api/token")
	fun getAuthToken(
		@Header("Authorization") authorization: String,
		@Field("grant_type") grantType: String
	): Call<AuthTokenResponse>

	@GET("v1/search")
	fun searchArtists(
		@Header("Authorization") authorization: String,
		@Query("q") query: String,
		@Query("type") type: String = ARTIST,
		@Query("limit") limit: Int = LIMIT_ONE
	): Call<SearchArtistsResponse>

	@GET("v1/artists/{id}/top-tracks")
	fun getArtistTopTracks(
		@Path("id") artistId: String,
		@Header("Authorization") authorization: String,
		@Query("market") market: String = US_COUNTRY_CODE
	): Call<ArtistTopTracksResponse>

	@GET("v1/audio-features/{id}")
	fun getTrackAudioFeatures(
		@Path("id") trackId: String,
		@Header("Authorization") authorization: String,
	): Call<TrackAudioFeatures>
}