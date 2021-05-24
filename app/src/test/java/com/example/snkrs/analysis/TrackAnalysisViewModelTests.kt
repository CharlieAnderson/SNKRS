package com.example.snkrs.analysis

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.snkrs.CoroutineTestRule
import com.github.mikephil.charting.data.RadarEntry
import com.snkrs.analysis.TrackAnalysisViewModel
import com.snkrs.network.MainRepository
import com.snkrs.network.models.Album
import com.snkrs.network.models.Track
import com.snkrs.network.models.TrackAudioFeatures
import com.snkrs.network.response.AuthTokenResponse
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URL


@ExperimentalCoroutinesApi
class TrackAnalysisViewModelTests {

	private lateinit var viewModel: TrackAnalysisViewModel
	private lateinit var repository: MainRepository

	private val mockAlbum = Album (
		name = "name",
		id = "id",
		images = listOf(mockk(), mockk(), mockk())
	)
	private val mockTrack = Track(
		id = "id",
		name = "name",
		artists = listOf(),
		album = mockAlbum,
		popularity = 0,
		previewUrl = URL("http://example")
	)
	private val mockAudioFeatures = TrackAudioFeatures(
		id = "id",
		danceability = 1.0,
		energy = 1.0,
		loudness = 1.0,
		acousticness = 1.0,
		valence = 1.0,
		tempo = 1.0,
		key = 0,
		duration = 1.0,
		instrumentalness = 1.0,
		liveness = 1.0
	)
	private val authToken = AuthTokenResponse(
		token = "token",
		tokenType = "tokenType",
		expiresIn = Integer.MAX_VALUE
	)
	private val testCoroutineDispatcher = TestCoroutineDispatcher()

	@ExperimentalCoroutinesApi
	@get:Rule
	var coroutinesRule = CoroutineTestRule()

	@get:Rule
	var instantExecutorRule = InstantTaskExecutorRule()

	@Before
	fun setup() {
		repository = mockk()
		viewModel = TrackAnalysisViewModel(testCoroutineDispatcher, repository)
	}

	@Test
	fun `getRadarEntries gives the correct adjusted values for each entry`() {
		// Given
		val expectedRadarDataEntries = mutableListOf(
			RadarEntry(100.0f),
			RadarEntry(100.0f),
			RadarEntry(100.0f),
			RadarEntry(100.0f),
			RadarEntry(100.0f),
			RadarEntry(100.0f)
		)
		// When
		val actualRadarEntries = viewModel.getRadarEntries(mockAudioFeatures)

		// Then
		for (index in actualRadarEntries.indices) {
			assertThat(actualRadarEntries[index].value, `is`(expectedRadarDataEntries[index].value))
		}
	}

	@Test
	fun `getSelectedTrackAnalysis populates LiveData`() = runBlockingTest {
		// Given
		val expectedTracks = listOf(mockTrack, mockTrack)
		val trackObserver: Observer<Track> = Observer {}
		coEvery { repository.getAuthToken() } returns authToken
		coEvery { repository.getArtistTopTracks(any()) } returns expectedTracks
		coEvery { repository.getTrackAudioFeatures(any()) } returns mockAudioFeatures

		// When
		viewModel.selectedTrackData.observeForever(trackObserver)
		viewModel.getSelectedTrackAnalysis(artistId = "id", trackIndex = 0)
		val actualSelectedTrack = viewModel.selectedTrackData.value

		// Then
		assertEquals(expectedTracks.first(), actualSelectedTrack)
	}

}