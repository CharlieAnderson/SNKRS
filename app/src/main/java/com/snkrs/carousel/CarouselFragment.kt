package com.snkrs.carousel

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.snkrs.R
import com.snkrs.base.BaseFragment
import com.snkrs.carousel.CarouselViewModel.Companion.ALBUM_IMAGE_EXTRA
import com.snkrs.carousel.CarouselViewModel.Companion.TITLE_TEXT_EXTRA
import com.snkrs.carousel.CarouselViewModel.Companion.TRACK_CARD_EXTRA
import com.snkrs.databinding.FragmentCarouselLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/***
 * Fragment used to show a carousel of an Artist's top tracks on Spotify
 */
class CarouselFragment : BaseFragment<CarouselViewModel, FragmentCarouselLayoutBinding>() {
	var carouselAdapter: CarouselAdapter? = null

	override fun setupListeners() {
		binding.searchEditText.setOnEditorActionListener(getOnEditorActionListener())
	}

	override fun observeData() {
		if (carouselAdapter == null) {
			setDefaultCarouselAdapter()
		}
		viewModel.topTracksData.observe(viewLifecycleOwner, { updateCarousel() })
		viewModel.artistData.observe(viewLifecycleOwner, { updateArtistTitle(it.name) })
	}

	fun convertDpToPixels(dp: Float): Float {
		val res = resources
		return TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			dp,
			res.displayMetrics
		)
	}

	/**
	 * sets up a default carousel with ?-mark views and hides the buttons.
	 */
	private fun setDefaultCarouselAdapter() {
		lifecycleScope.launch(Dispatchers.Main) {
			carouselAdapter = CarouselAdapter()
			binding.fragmentMotionLayout.carousel.setAdapter(carouselAdapter)
		}
	}

	/**
	 * updates the title with artist's name at the top.
	 */
	private fun updateArtistTitle(artistName: String) {
		binding.artistTitleText.text = getString(R.string.artist_title_placeholder, artistName)
	}

	/**
	 * updates the carousel with the Artist's top track data.
	 */
	private fun updateCarousel() {
		lifecycleScope.launch(Dispatchers.Main) {
			carouselAdapter = CarouselAdapter(
				images = viewModel.getImageBitmapsAsync().await() ?: listOf(),
				trackNames = viewModel.getTopTrackNames() ?: listOf(),
				buttonOnClick = getAnalysisButtonClickListener(),
				convertDpToPixels = ::convertDpToPixels
			)
			binding.fragmentMotionLayout.carousel.setAdapter(carouselAdapter)
			binding.fragmentMotionLayout.carousel.refresh()
		}
	}

	/**
	 * click listener for the analysis button. navigates to the [TrackAnalysisFragment].
	 */
	private fun getAnalysisButtonClickListener() = View.OnClickListener {
		// passes Navigator extras for shared element animation
		val extras = FragmentNavigatorExtras(
			binding.artistTitleText to TITLE_TEXT_EXTRA,
			binding.fragmentMotionLayout.carouselItem3.centerCard to TRACK_CARD_EXTRA,
			binding.fragmentMotionLayout.carouselItem3.itemImage to ALBUM_IMAGE_EXTRA
		)
		findNavController().navigate(
			R.id.action_carouselFragment_to_trackAnalysisFragment,
			viewModel.getAnalysisBundle(
				binding.fragmentMotionLayout.carouselItem3.itemText.text.toString()
			),
			null,
			extras
		)
	}

	/**
	 * action listener for the artist search bar.
	 * Closes keyboard when search entered and uses query to search spotify for first artist.
	 */
	private fun getOnEditorActionListener() = TextView.OnEditorActionListener {
		textView, actionId, _ ->
		when (actionId) {
			EditorInfo.IME_ACTION_DONE -> {
				viewModel.getArtistAndTrackData(query = textView.text.toString())
				val inputMethodManager =
					context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
				inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
				true
			}
			else -> false
		}
	}

	override fun getViewModelClass() = CarouselViewModel::class.java

	override fun getViewBinding() = FragmentCarouselLayoutBinding.inflate(layoutInflater)
}