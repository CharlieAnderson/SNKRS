package com.snkrs.carousel

import android.app.Activity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
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

class CarouselFragment : BaseFragment<CarouselViewModel, FragmentCarouselLayoutBinding>() {
	var carouselAdapter: CarouselAdapter? = null

	override fun setupListeners() {
		binding.searchEditText.setOnEditorActionListener(getOnEditorActionListener())
		if (carouselAdapter == null) {
			setDefaultCarouselAdapter()
		}
	}

	override fun observeData() {
		viewModel.topTracksData.observe(viewLifecycleOwner, { updateCarousel() })
		viewModel.artistData.observe(viewLifecycleOwner, { updateArtistTitle(it.name) })
	}
	
	private fun setDefaultCarouselAdapter() {
		val defaultBitmap = context?.resources?.let {
			viewModel.getDrawableBitMap(it, android.R.drawable.ic_menu_help)
		}
		carouselAdapter = CarouselAdapter(
			listOf(defaultBitmap, defaultBitmap, defaultBitmap, defaultBitmap, defaultBitmap),
		)
		binding.fragmentMotionLayout.carousel.setAdapter(carouselAdapter)
	}

	private fun updateArtistTitle(artistName: String) {
		binding.artistTitleText.text = getString(R.string.artist_title_placeholder, artistName)
	}

	private fun updateCarousel() {
		GlobalScope.launch(Dispatchers.Main) {
			carouselAdapter = CarouselAdapter(
				images = viewModel.getImageBitmapsAsync().await() ?: listOf(),
				trackNames = viewModel.getTopTrackNames() ?: listOf(),
				getAnalysisButtonClickListener()
			)
			binding.fragmentMotionLayout.carousel.setAdapter(carouselAdapter)
			binding.fragmentMotionLayout.carousel.refresh()
		}
	}

	private fun getAnalysisButtonClickListener() = View.OnClickListener {
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