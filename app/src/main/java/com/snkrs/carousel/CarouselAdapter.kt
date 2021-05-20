package com.snkrs.carousel

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Carousel
import com.snkrs.R

class CarouselAdapter(
	val images: List<Bitmap?>? = null,
	val trackNames: List<String>? = null,
	val buttonOnClick: View.OnClickListener? = null
	): Carousel.Adapter {
	companion object {
		const val NO_ELEVATION = 0f
		const val IMAGE_ELEVATION = 12f
	}

	override fun count(): Int {
		return images?.size ?: 0
	}

	override fun populate(view: View, index: Int) {
		view.findViewById<ImageView>(R.id.item_image)?.setImageBitmap(images?.get(index))
		view.findViewById<TextView>(R.id.item_text)?.text = trackNames?.get(index)
		view.findViewById<TextView>(R.id.item_analysis_button).setOnClickListener(buttonOnClick)
		// Data is not ready yet when button is disabled
		if (buttonOnClick == null) {
			view.findViewById<ImageView>(R.id.item_image)?.elevation = NO_ELEVATION
			view.findViewById<TextView>(R.id.item_analysis_button).visibility = View.INVISIBLE
		} else {
			view.findViewById<TextView>(R.id.item_analysis_button).visibility = View.VISIBLE
			view.findViewById<ImageView>(R.id.item_image)?.elevation = IMAGE_ELEVATION
		}
	}

	override fun onNewItem(index: Int) {
		println(index)
	}
}