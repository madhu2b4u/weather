import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.demo.core.di.Result
import com.demo.core.weather.WeatherScreenState
import com.demo.core.weather.model.Condition
import com.demo.core.weather.model.Current
import com.demo.core.weather.model.Location
import com.demo.core.weather.model.WeatherInfo
import com.demo.core.weather.usecase.WeatherInfoUseCase
import com.demo.weather.presentation.viewmodel.WeatherInfoViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.advanceUntilIdle

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherInfoViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var weatherInfoUseCase: WeatherInfoUseCase
    private lateinit var viewModel: WeatherInfoViewModel

    private val sampleWeatherInfo = WeatherInfo(
        current = Current(
            condition = Condition(1000, "icon_url", "Sunny"),
            feelslike_c = 25.0,
            humidity = 60,
            temp_c = 28.0,
            uv = 5.0
        ),
        location = Location(
            name = "Auckland",
            region = "Auckland Region",
            country = "New Zealand",
            lat = -36.8485,
            lon = 174.7633
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        weatherInfoUseCase = mockk(relaxed = true)  // Using relaxed mock to handle unexpected calls
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test successful weather info fetch`() = runTest {
        // Given
        coEvery { weatherInfoUseCase.getWeatherInfo(any()) } returns flowOf(Result.Success(sampleWeatherInfo))

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue("Expected Success state but was ${currentState::class.simpleName}", currentState is WeatherScreenState.Success)
        currentState as WeatherScreenState.Success
        assertEquals("Auckland", currentState.weatherInfo.location.name)
        assertEquals(28.0, currentState.weatherInfo.current.temp_c, 0.001) // Using delta for floating-point comparison
        assertEquals(25.0, currentState.weatherInfo.current.feelslike_c, 0.001)
        assertEquals(5.0, currentState.weatherInfo.current.uv, 0.001)
    }

    @Test
    fun `test error state when api call fails`() = runTest {
        // Given
        val errorMessage = "Network error occurred"
        coEvery { weatherInfoUseCase.getWeatherInfo(any()) } returns flowOf(
            Result.Loading,
            Result.Error(errorMessage)
        )

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue("Expected Error state but was ${currentState::class.simpleName}", currentState is WeatherScreenState.Error)
        currentState as WeatherScreenState.Error
        assertEquals(errorMessage, currentState.message)
    }

    /*@Test
    fun `test loading state when fetching weather info`() = runTest {
        // Given
        coEvery { weatherInfoUseCase.getWeatherInfo(any()) } returns flowOf(
            Result.Loading,
            Result.Success(sampleWeatherInfo) // Adding success state to complete the flow
        )

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase)

        // Then
        val initialState = viewModel.uiState.value
        assertTrue(
            "Expected Loading state but was ${initialState::class.simpleName}",
            initialState is WeatherScreenState.Loading
        )
    }*/

    /*@Test
    fun `test exception handling during weather fetch`() = runTest {
        // Given
        coEvery { weatherInfoUseCase.getWeatherInfo(any()) } answers {
            throw RuntimeException("Network error")
        }

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue("Expected Error state but was ${currentState::class.simpleName}", currentState is WeatherScreenState.Error)
        currentState as WeatherScreenState.Error
        assertEquals("Failed to fetch weather: Network error", currentState.message)
    }*/

    @Test
    fun `test fetching weather info with specific city`() = runTest {
        // Given
        val city = "Wellington"
        val wellingtonWeatherInfo = sampleWeatherInfo.copy(
            location = sampleWeatherInfo.location.copy(name = city)
        )
        coEvery { weatherInfoUseCase.getWeatherInfo(eq(city)) } returns flowOf(
            Result.Loading,
            Result.Success(wellingtonWeatherInfo)
        )

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase)
        viewModel.fetchWeatherInfo(city)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue("Expected Success state but was ${currentState::class.simpleName}", currentState is WeatherScreenState.Success)
        currentState as WeatherScreenState.Success
        assertEquals(city, currentState.weatherInfo.location.name)
    }

    @Test
    fun `test initial empty state`() = runTest {
        // Given
        coEvery { weatherInfoUseCase.getWeatherInfo(any()) } returns flowOf(
            Result.Empty("No Data", "Please enter a city")
        )

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue("Expected Empty state but was ${currentState::class.simpleName}", currentState is WeatherScreenState.Empty)
        currentState as WeatherScreenState.Empty
        assertEquals("No Data", currentState.title)
        assertEquals("Please enter a city", currentState.message)
    }
}