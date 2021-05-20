package com.snkrs.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionInflater
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.snkrs.R
import com.snkrs.base.BaseFragment
import com.snkrs.databinding.FragmentAnalysisLayoutBinding
import com.snkrs.network.models.Track
import com.snkrs.network.response.TrackAudioFeaturesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


class TrackAnalysisFragment: BaseFragment<TrackAnalysisViewModel, FragmentAnalysisLayoutBinding>() {
	companion object {
		const val featureMultiplier = 100
		const val loudnessMultiplier = 10
		val audioAnalysisLabels = arrayOf(
			"Danceability",
			"Energy",
			"Positivity",
			"Acousticness",
			"Liveness",
			"Loudness",
			"Instrumentalness"
		)
	}

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
		setupRadarChart()
	}

	override fun observeData() {
		viewModel.selectedTrackData.observe(viewLifecycleOwner, { setHeader(it) })
		viewModel.trackAnalysisData.observe(viewLifecycleOwner, { setupRadarChartData(it) })
		viewModel.getSelectedTrackAnalysis(
			artistId = arguments?.getString("artistId")!!,
			trackIndex = arguments?.getInt("trackIndex")!!
		)
	}

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

	fun setupRadarChart() = binding.radarChart.apply {
		webColor = resources.getColor(R.color.dark_grey)
		webColorInner = resources.getColor(R.color.dark_grey)
		webLineWidth = 2f
		webLineWidthInner = 2f
		webAlpha = 40
		description.isEnabled = false
		isHighlightPerTapEnabled = true
		setTouchEnabled(true)
		animateXY(600, 600, Easing.EaseInOutQuad)
		yAxis.isEnabled = false
		xAxis.textSize = 15f
		xAxis.textColor = resources.getColor(R.color.primary_purple)
		xAxis.valueFormatter = object: ValueFormatter() {
			override fun getFormattedValue(value: Float): String {
				return audioAnalysisLabels[value.toInt() % audioAnalysisLabels.size]
			}
		}
		legend.isEnabled = false
	}

	private fun setupRadarChartData(trackAnalysis: TrackAudioFeaturesResponse) {
		val dataset = getDataSet(getRadarEntries(trackAnalysis))
		val data = RadarData(dataset).apply {
			setValueFormatter(object: ValueFormatter() {
				override fun getFormattedValue(value: Float): String {
					return value.roundToInt().toString()
				}
			})
			setValueTextSize(15f)
			setValueTextColor(resources.getColor(R.color.black))
			isHighlightEnabled = true
			setDrawValues(true)
		}
		binding.radarChart.data = data
	}

	private fun getRadarEntries(trackAnalysis: TrackAudioFeaturesResponse) = listOf(
		RadarEntry(trackAnalysis.danceability.toFloat() * featureMultiplier),
		RadarEntry(trackAnalysis.energy.toFloat() * featureMultiplier),
		RadarEntry(trackAnalysis.valence.toFloat() * featureMultiplier),
		RadarEntry(trackAnalysis.acousticness.toFloat() * featureMultiplier),
		RadarEntry(trackAnalysis.liveness.toFloat() * featureMultiplier),
		RadarEntry(trackAnalysis.loudness.toFloat().absoluteValue * loudnessMultiplier),
		RadarEntry(trackAnalysis.instrumentalness.toFloat() * featureMultiplier)
	)

	private fun getDataSet(entries: List<RadarEntry>) =
		RadarDataSet(entries, "Song Analysis").apply {
			color = resources.getColor(android.R.color.holo_red_dark)
			fillColor = resources.getColor(android.R.color.holo_red_dark)
			fillAlpha = 50
			lineWidth = 2f
			setDrawFilled(true)
			isDrawHighlightCircleEnabled = true
			highlightCircleFillColor = resources.getColor(R.color.seafoam_secondary)
			highlightCircleInnerRadius = 0f
			highlightCircleOuterRadius = 4f
			highlightCircleStrokeWidth = 6f
			highlightCircleStrokeColor = resources.getColor(R.color.green_secondary)
			highLightColor = 0
		}

	override fun getViewModelClass() = TrackAnalysisViewModel::class.java

	override fun getViewBinding() = FragmentAnalysisLayoutBinding.inflate(layoutInflater)
}