package com.snkrs.network.models

import com.google.gson.annotations.SerializedName

data class TrackAudioFeatures(
	val id: String,
	val danceability: Double,
	val energy: Double,
	val loudness: Double,
	val acousticness: Double,
	val instrumentalness: Double,
	val liveness: Double,
	val valence: Double,
	val tempo: Double,
	@SerializedName("duration_ms")
	val duration: Double,
	val key: Int
)
