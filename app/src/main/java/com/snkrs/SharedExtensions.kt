package com.snkrs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import java.io.IOException
import java.net.URL

fun URL.toBitmap(): Bitmap? {
	return try {
		BitmapFactory.decodeStream(openStream())
	} catch (exception: IOException) {
		println(exception)
		null
	}
}
