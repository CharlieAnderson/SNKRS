package com.example.snkrs.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.snkrs.CoroutineTestRule
import com.snkrs.carousel.CarouselViewModel
import com.snkrs.network.MainRemote
import com.snkrs.network.MainRepository
import com.snkrs.network.SpotifyApi
import com.snkrs.network.models.*
import com.snkrs.network.response.ArtistTopTracksResponse
import com.snkrs.network.response.AuthTokenResponse
import com.snkrs.network.response.SearchArtistsResponse
import io.mockk.*
import junit.framework.Assert
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import retrofit2.await
import java.net.URL
import java.util.stream.DoubleStream.builder

@ExperimentalCoroutinesApi
class MainRepositoryTests {
	private lateinit var repository: MainRepository
	private lateinit var mockRemote: MainRemote

	private val expectedAuthTokenResponse = AuthTokenResponse(
		token = "token",
		tokenType = "tokenType",
		expiresIn = 3600
	)

	@ExperimentalCoroutinesApi
	@get:Rule
	var coroutinesRule = CoroutineTestRule()

	@get:Rule
	var instantExecutorRule = InstantTaskExecutorRule()

	@Before
	fun setup() {
		mockkStatic("retrofit2.KotlinExtensions")

		mockRemote = mockk()
		every { mockRemote.spotifyBaseApi } returns mockk()
		repository = MainRepository(mockRemote)


		coEvery { mockRemote.getAuthToken().await() } returns expectedAuthTokenResponse

	}

	@Test
	fun `getAuthToken returns a valid authorization token`() = runBlockingTest {
		// When
		val actualAuthTokenResponse = repository.getAuthToken()

		// Then
		assertFalse(repository.isTokenExpired())
		assertEquals(expectedAuthTokenResponse, actualAuthTokenResponse)
	}

	@Test
	fun `searchForArtist returns expected Artist`() = runBlockingTest {
		// Given
		val expectedArtist = Artist(
			name = "name",
			id = "id",
			popularity = 0,
			followers = Followers("href", 0),
			genres = listOf("genre"),
			images = listOf(mockk(), mockk(), mockk())
		)
		val expectedSearchResponse = SearchArtistsResponse(Artists(listOf(expectedArtist)))
		coEvery { mockRemote.searchArtists(any(), any()).await() } returns
			expectedSearchResponse

		// When
		val actualArtist = repository.searchForArtist("some artist")

		// Then
		assertEquals(expectedArtist, actualArtist)
	}

	@Test
	fun `getArtistTopTracks returns expected list of tracks`() = runBlockingTest {
		// Given
		val expectedTracks = listOf(Track(
			name = "name",
			id = "id",
			artists = listOf(),
			album = mockk(),
			popularity = 0,
			previewUrl = mockk()
		))
		val expectedArtistTopTracksResponse = ArtistTopTracksResponse(expectedTracks)
		coEvery { mockRemote.getArtistTopTracks(any(), any()).await() } returns
			ArtistTopTracksResponse(listOf())
		coEvery { mockRemote.getArtistTopTracks(any(), "id").await() } returns
			expectedArtistTopTracksResponse

		// When
		val actualTopTracks = repository.getArtistTopTracks("id")
		val actualTopTracksNoId = repository.getArtistTopTracks("")

		// Then
		assertEquals(expectedTracks, actualTopTracks)
		assertNotSame(expectedTracks, actualTopTracksNoId)
	}

	@Test
	fun ` getAudioFeatures returns expected audio features`() = runBlockingTest {
		// Given
		val expectedAudioFeatures = TrackAudioFeatures(
			id = "id",
			danceability = 0.0,
			energy = 0.0,
			loudness = 0.0,
			acousticness = 0.0,
			instrumentalness = 0.0,
			liveness = 0.0,
			valence = 0.0,
			tempo = 0.0,
			duration = 0.0,
			key = 0
		)
		coEvery { mockRemote.getTrackAudioFeatures(any(), any()).await() } returns
			mockk()
		coEvery { mockRemote.getTrackAudioFeatures(any(), "id").await() } returns
			expectedAudioFeatures

		// When
		val actualAudioFeatures = repository.getTrackAudioFeatures("id")
		val actualAudioFeaturesNoId = repository.getTrackAudioFeatures("")

		// Then
		assertEquals(expectedAudioFeatures, actualAudioFeatures)
		assertNotSame(expectedAudioFeatures, actualAudioFeaturesNoId)
	}

}