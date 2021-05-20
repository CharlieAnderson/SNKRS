package com.snkrs.network.response

import com.google.gson.annotations.SerializedName

data class AuthTokenResponse(
	@SerializedName("access_token")
	val token: String,
	@SerializedName("token_type")
	val tokenType: String,
	@SerializedName("expires_in")
	val expiresIn: Int // Token expires in 3600 seconds or 1 Hour
)
