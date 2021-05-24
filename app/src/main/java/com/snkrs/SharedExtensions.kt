package com.snkrs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.net.URL

/**
 * Shared extension methods that may be useful through codebase
 */
fun URL.toBitmap(): Bitmap? {
	return try {
		BitmapFactory.decodeStream(openStream())
	} catch (exception: IOException) {
		println(exception)
		null
	}
}


fun String.encodeToBase64(): String {
	return java.util.Base64.getEncoder().encodeToString(this.toByteArray());
}

