package com.snkrs.analysis

import android.content.res.Resources
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.snkrs.R
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class RadarChartHelper(private val radarChart: RadarChart, private val resources: Resources) {
	companion object {
		const val DATASET_LABEL = "Song Analysis"
	}

	fun setupRadarChart() = radarChart.apply {
		webColor = resources.getColor(R.color.dark_grey)
		webColorInner = resources.getColor(R.color.dark_grey)
		webLineWidth = 2f
		webLineWidthInner = 2f
		webAlpha = 50
		description.isEnabled = false
		isHighlightPerTapEnabled = true
		setTouchEnabled(true)
		animateXY(600, 600, Easing.EaseInOutBounce)
		legend.isEnabled = false
		setupAxes(this)
	}

	fun bindDataToChart(entries: List<RadarEntry>) {
		val dataset = setupDataSet(entries)
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
		radarChart.data = data
		radarChart.invalidate()
	}

	private fun setupAxes(radarChart: RadarChart) {
		radarChart.yAxis.apply {
			setLabelCount(6, true)
			axisMaximum = 100f
			axisMinimum = 0f
			setDrawLabels(false)
		}
		radarChart.xAxis.apply {
			textSize = 15f
			axisMaximum = 0f
			axisMinimum = 0f
			calculate(axisMinimum, axisMaximum)
			position = XAxis.XAxisPosition.BOTTOM_INSIDE
			textColor = resources.getColor(R.color.primary_purple)
			valueFormatter = object: ValueFormatter() {
				override fun getFormattedValue(value: Float): String {
					return TrackAnalysisFragment.audioAnalysisLabels[value.toInt() % TrackAnalysisFragment.audioAnalysisLabels.size]
				}
			}
		}
	}

	private fun setupDataSet(entries: List<RadarEntry>) =
		RadarDataSet(entries, DATASET_LABEL).apply {
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
}