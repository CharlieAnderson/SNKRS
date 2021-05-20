package com.snkrs.carousel

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Carousel
import com.snkrs.R

/**
 * adapter to used to set and format the items in the Carousel layout
 */
class CarouselAdapter(
	val images: List<Bitmap?>? = null,
	val trackNames: List<String>? = null,
	val buttonOnClick: View.OnClickListener? = null,
	val convertDpToPixels: ((Float) -> Float)? = null
	): Carousel.Adapter {
	companion object {
		const val NO_ELEVATION = 0f
		const val IMAGE_ELEVATION_DP = 16f
		const val BUTTON_ELEVATION_DP = 12f
	}

	override fun count(): Int {
		return images?.size ?: 0
	}

	/**
	 * populates and adjusts the carousel layout with cards representing the elements..
	 * If the button click listener is not passed,
	 * the data will be assumed to not be ready yet for the analysis fragment.
	 */
	override fun populate(view: View, index: Int) {
		val image = view.findViewById<ImageView>(R.id.item_image)
		val text = view.findViewById<TextView>(R.id.item_text)
		val button = view.findViewById<TextView>(R.id.item_analysis_button)
		image?.setImageBitmap(images?.get(index))
		text?.text = trackNames?.get(index)
		view.findViewById<TextView>(R.id.item_analysis_button).setOnClickListener(buttonOnClick)
		// Data is not ready yet when button is disabled, hide button and remove shadows
		if (buttonOnClick == null) {
			button?.visibility = View.INVISIBLE
			button?.elevation = NO_ELEVATION
			image?.elevation = NO_ELEVATION
		} else {
			// Data is ready, show button, and adjust elevation with calculation
			button?.visibility = View.VISIBLE
			button?.elevation = convertDpToPixels?.invoke(BUTTON_ELEVATION_DP) ?: NO_ELEVATION
			image?.elevation = convertDpToPixels?.invoke(IMAGE_ELEVATION_DP) ?: NO_ELEVATION
		}
	}

	override fun onNewItem(index: Int) {
		// No-Op
	}
}