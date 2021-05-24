package com.example.snkrs.carousel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import com.example.snkrs.CoroutineTestRule
import com.snkrs.carousel.CarouselViewModel
import com.snkrs.network.MainRepository
import com.snkrs.network.models.*
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URL

@ExperimentalCoroutinesApi
class CarouselViewModelTests {
	private lateinit var viewModel: CarouselViewModel
	private lateinit var repository: MainRepository
	private val testCoroutineDispatcher = TestCoroutineDispatcher()


	private val mockArtist = Artist(
		name = "name",
		id = "id",
		popularity = 0,
		followers = Followers(href = "href", total = 0),
		genres = listOf("genre"),
		images = listOf(mockk(), mockk(), mockk())
	)
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
	private val mockTopTracks = listOf(mockTrack, mockTrack, mockTrack)

	@ExperimentalCoroutinesApi
	@get:Rule
	var coroutinesRule = CoroutineTestRule()

	@get:Rule
	var instantExecutorRule = InstantTaskExecutorRule()

	@Before
	fun setup() {
		repository = mockk()
		viewModel = CarouselViewModel(testCoroutineDispatcher, repository)
	}

	@Test
	fun `getArtistAndTrackData populates LiveData`() = runBlockingTest {
		// Given
		coEvery { repository.searchForArtist(any()) } returns mockArtist
		coEvery { repository.getArtistTopTracks(any()) } returns mockTopTracks

		// When
		viewModel.getArtistAndTrackData("any artist, idk")
		val actualArtist = viewModel.artistData.value
		val actualTopTracks = viewModel.topTracksData.value

		// Then
		assertEquals(mockArtist, actualArtist)
		assertEquals(mockTopTracks, actualTopTracks)
	}

	@Test
	fun `getTopTrackNames returns a list of the top tracks' names`() = runBlockingTest {
		// Given
		val expectedTopTracksNames = listOf("name", "name", "name")
		coEvery { repository.searchForArtist(any()) } returns mockArtist
		coEvery { repository.getArtistTopTracks(any()) } returns mockTopTracks

		// When
		viewModel.getArtistAndTrackData("any artist, idk")
		val actualTopTracksNames = viewModel.getTopTrackNames()

		// Then
		assertEquals(expectedTopTracksNames, actualTopTracksNames)
	}

	@Test
	fun `getTrackIndex returns index of the first matching track`() = runBlockingTest {
		// Given
		val expectedTrackIndex = 0
		coEvery { repository.searchForArtist(any()) } returns mockArtist
		coEvery { repository.getArtistTopTracks(any()) } returns mockTopTracks
		// When
		viewModel.getArtistAndTrackData("any artist, idk")
		val actualTrackIndex = viewModel.getTrackIndex("name")
		// Then
		assertEquals(expectedTrackIndex, actualTrackIndex)
	}

}