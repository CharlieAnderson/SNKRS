package com.snkrs.network.models

import com.google.gson.annotations.SerializedName
import java.net.URL

data class Track(
	val name: String,
	val id: String,
	val artists: List<Artist>,
	val album: Album,
	val popularity: Int,
	@SerializedName("preview_url")
	val previewUrl: URL
)
