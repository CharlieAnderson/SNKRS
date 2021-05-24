package com.snkrs.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.snkrs.analysis.TrackAnalysisViewModel
import com.snkrs.carousel.CarouselViewModel
import com.snkrs.network.MainRepository
import kotlinx.coroutines.CoroutineDispatcher

/**
 * ViewModel Factory used to facilitate constructor dependency injection for each ViewModel
 */
class ViewModelFactory(
	private val dispatcher: CoroutineDispatcher,
	private val repository: MainRepository
) : ViewModelProvider.Factory {
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		@Suppress("UNCHECKED_CAST")
		return when (modelClass) {
			CarouselViewModel::class.java -> buildCarouselViewModel(modelClass) as T
			TrackAnalysisViewModel::class.java -> buildTrackAnalysisViewModel(modelClass) as T
			else -> throw IllegalArgumentException("Unable to construct ViewModel")
		}
	}
	   
	private fun buildCarouselViewModel(modelClass: Class<CarouselViewModel>) =
		modelClass.getConstructor(
			CoroutineDispatcher::class.java,
			MainRepository::class.java
		).newInstance(dispatcher, repository)

	private fun buildTrackAnalysisViewModel(modelClass: Class<TrackAnalysisViewModel>) =
		modelClass.getConstructor(
			CoroutineDispatcher::class.java,
			MainRepository::class.java
		).newInstance(dispatcher, repository)
}