package com.example.snkrs.di

import androidx.lifecycle.ViewModel
import com.snkrs.analysis.TrackAnalysisViewModel
import com.snkrs.carousel.CarouselViewModel
import com.snkrs.di.ViewModelFactory
import com.snkrs.network.MainRepository
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class ViewModelFactoryTests {

	private lateinit var viewModelFactory: ViewModelFactory
	private val mockDispatcher: CoroutineDispatcher = mockk()
	private val mockRepository: MainRepository = mockk()


	@Before
	fun setup() {
		viewModelFactory = ViewModelFactory(
			mockDispatcher,
			mockRepository
		)
	}

	@Test
	fun `buildCarouselViewModel returns CarouselViewModel with correct arguments`() {
		// When
		val viewModel = viewModelFactory.create(CarouselViewModel::class.java)

		// Then
		assertEquals(mockDispatcher, viewModel.dispatcher)
		assertEquals(mockRepository, viewModel.repository)
	}

	@Test
	fun `buildTrackAnalysisViewModel returns CarouselViewModel with correct arguments`() {
		// When
		val viewModel = viewModelFactory.create(TrackAnalysisViewModel::class.java)

		// Then
		assertEquals(mockDispatcher, viewModel.dispatcher)
		assertEquals(mockRepository, viewModel.repository)
	}

	@Test(expected = IllegalArgumentException::class)
	fun `invalid viewModel class throws Exception`() {
		// Given
		val expectedMessage = "Unable to construct ViewModel"

		// When
		viewModelFactory.create(ViewModel::class.java)

		// Then
		val exception = assertThrows(IllegalArgumentException::class.java) {
			Integer.parseInt("1a");
		}
		assertEquals(expectedMessage, exception.message)
	}
}