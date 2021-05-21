package com.snkrs.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionInflater
import com.snkrs.R
import com.snkrs.base.BaseFragment
import com.snkrs.databinding.FragmentAnalysisLayoutBinding
import com.snkrs.network.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar


/**
 *  Fragment to display a radar chart representing the Audio features of the selected track.
 */
class TrackAnalysisFragment: BaseFragment<TrackAnalysisViewModel, FragmentAnalysisLayoutBinding>() {
	companion object {
		val audioAnalysisLabels = arrayOf(
			"Instrumentalness",
			"Energy",
			"Positivity",
			"Acousticness",
			"Liveness",
			"Danceability",
		)
	}

	private var chartHelper: RadarChartHelper? = null

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		sharedElementEnterTransition =
			TransitionInflater.from(context).inflateTransition(android.R.transition.move)
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun setupViews() {
		chartHelper = context?.resources?.let { RadarChartHelper(binding.radarChart, it) }
		chartHelper?.setupRadarChart()
	}

	override fun observeData() {
		viewModel.selectedTrackData.observe(viewLifecycleOwner, { setHeader(it) })
		viewModel.radarDataEntries.observe(viewLifecycleOwner, {
			chartHelper?.bindDataToChart(it)
		})

		viewModel.getSelectedTrackAnalysis(
			artistId = arguments?.getString("artistId")!!,
			trackIndex = arguments?.getInt("trackIndex")!!
		)
	}

	/**
	 * sets up the artist/album/track name text, as well as the album cover.
	 */
	private fun setHeader(track: Track) {
		binding.analysisTitle.text = getString(
			R.string.track_album_artist_placeholder,
			track.name,
			track.album.name,
			track.artists.first().name,
		)
		GlobalScope.launch(Dispatchers.Main) {
			binding.analysisImage.setImageBitmap(viewModel.getImageBitmapAsync().await())
		}
	}

	override fun getViewModelClass() = TrackAnalysisViewModel::class.java

	override fun getViewBinding() = FragmentAnalysisLayoutBinding.inflate(layoutInflater)
}