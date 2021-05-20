package com.snkrs.network.models

data class Artist(
	val name: String,
	val id: String,
	val popularity: Int,
	val followers: Followers,
	val genres: ArrayList<String>,
	val images: ArrayList<Image>,
)
