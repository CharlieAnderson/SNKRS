package com.snkrs.network.models

import com.google.gson.annotations.SerializedName

data class Artists (
	@SerializedName("items")
	val artist: List<Artist>
)