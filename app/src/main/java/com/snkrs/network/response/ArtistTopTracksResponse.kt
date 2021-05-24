package com.snkrs.network.response

import com.snkrs.network.models.Track

data class ArtistTopTracksResponse(
	val tracks: List<Track>
)