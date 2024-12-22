import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.demo.core.DataStoreManager
import com.demo.core.di.Result
import com.demo.core.weather.WeatherScreenState
import com.demo.core.weather.model.Condition
import com.demo.core.weather.model.Current
import com.demo.core.weather.model.Location
import com.demo.core.weather.model.WeatherInfo
import com.demo.core.weather.usecase.WeatherInfoUseCase
import com.demo.weather.presentation.viewmodel.WeatherInfoViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherInfoViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var weatherInfoUseCase: WeatherInfoUseCase
    private lateinit var dataStore: DataStoreManager
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
        weatherInfoUseCase = mockk()
        dataStore = mockk()

        // Default behavior for DataStore
        coEvery { dataStore.getCity() } returns "Auckland"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when initialized with valid city in DataStore, fetches weather successfully`() = runTest {
        // Given
        coEvery { dataStore.getCity() } returns "Auckland"
        coEvery { weatherInfoUseCase.getWeatherInfo("Auckland") } returns flowOf(
            Result.Success(sampleWeatherInfo)
        )

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase, dataStore)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue(currentState is WeatherScreenState.Success)
        assertEquals("Auckland", (currentState as WeatherScreenState.Success).weatherInfo.location.name)
        coVerify { dataStore.getCity() }
    }

    @Test
    fun `when DataStore returns null city, shows error state`() = runTest {
        // Given
        coEvery { dataStore.getCity() } returns null

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase, dataStore)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue(currentState is WeatherScreenState.Error)
        assertEquals("No city selected", (currentState as WeatherScreenState.Error).message)
    }

    @Test
    fun `when weather fetch fails, shows error state`() = runTest {
        // Given
        val errorMessage = "Network error occurred"
        coEvery { dataStore.getCity() } returns "Auckland"
        coEvery { weatherInfoUseCase.getWeatherInfo("Auckland") } returns flowOf(
            Result.Error(errorMessage)
        )

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase, dataStore)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue(currentState is WeatherScreenState.Error)
        assertEquals(errorMessage, (currentState as WeatherScreenState.Error).message)
    }

    @Test
    fun `when fetchWeatherInfo called manually, updates state correctly`() = runTest {
        // Given
        coEvery { dataStore.getCity() } returns "Wellington"
        coEvery { weatherInfoUseCase.getWeatherInfo("Wellington") } returns flowOf(
            Result.Success(sampleWeatherInfo.copy(
                location = sampleWeatherInfo.location.copy(name = "Wellington")
            ))
        )

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase, dataStore)
        viewModel.fetchWeatherInfo()
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue(currentState is WeatherScreenState.Success)
        assertEquals("Wellington", (currentState as WeatherScreenState.Success).weatherInfo.location.name)
    }

    @Test
    fun `when weather fetch throws exception, shows error state`() = runTest {
        // Given
        coEvery { dataStore.getCity() } returns "Auckland"
        coEvery { weatherInfoUseCase.getWeatherInfo("Auckland") } returns flow {
            throw RuntimeException("Network error")
        }

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase, dataStore)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue(currentState is WeatherScreenState.Error)
        assertEquals("Failed to fetch weather: Network error", (currentState as WeatherScreenState.Error).message)
    }

    @Test
    fun `verifies loading state when fetching weather info`() = runTest {
        // Given
        coEvery { dataStore.getCity() } returns "Auckland"
        coEvery { weatherInfoUseCase.getWeatherInfo("Auckland") } returns flowOf(
            Result.Loading,
            Result.Success(sampleWeatherInfo)
        )

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase, dataStore)

        // Then - capture initial loading state
        assertEquals(WeatherScreenState.Loading, viewModel.uiState.value)

        // Then - advance to final state
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is WeatherScreenState.Success)
    }

    @Test
    fun `when empty result returned, shows empty state`() = runTest {
        // Given
        coEvery { dataStore.getCity() } returns "Auckland"
        coEvery { weatherInfoUseCase.getWeatherInfo("Auckland") } returns flowOf(
            Result.Empty("No Data", "Please try again")
        )

        // When
        viewModel = WeatherInfoViewModel(weatherInfoUseCase, dataStore)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue(currentState is WeatherScreenState.Empty)
        currentState as WeatherScreenState.Empty
        assertEquals("No Data", currentState.title)
        assertEquals("Please try again", currentState.message)
    }
}