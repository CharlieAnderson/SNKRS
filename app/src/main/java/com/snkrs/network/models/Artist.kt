package com.snkrs.network.models

data class Artist(
	val name: String,
	val id: String,
	val popularity: Int,
	val followers: Followers,
	val genres: List<String>,
	val images: List<Image>,
)
