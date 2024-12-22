import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.demo.core.di.Result
import com.demo.core.weather.WeatherScreenState
import com.demo.core.weather.model.Location
import com.demo.core.weather.model.WeatherInfo
import com.demo.core.weather.usecase.WeatherInfoUseCase
import com.demo.search.domain.SearchUseCase
import com.demo.search.presentation.viewmodel.SearchScreenState
import com.demo.search.presentation.viewmodel.SearchViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SearchViewModel
    private val searchUseCase: SearchUseCase = mockk()
    private val weatherInfoUseCase: WeatherInfoUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(searchUseCase, weatherInfoUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchCity sets searchState to Loading and Success when cities are returned`() = testScope.runTest {
        // Arrange
        val cities = mutableListOf(
            Location("New Zealand", -36.8485, 174.7633, "Auckland", "Auckland")
        )
        val successResult = Result.success(cities)
        val weatherInfo = WeatherInfo(
            current = mockk(),
            location = mockk()
        )
        coEvery { searchUseCase.getSearchResults("Auckland") } returns flowOf(successResult)
        coEvery { weatherInfoUseCase.getWeatherInfo("Auckland") } returns flowOf(Result.success(weatherInfo))

        // Act
        viewModel.searchCity("Auckland")
        advanceUntilIdle()

        // Assert
        val searchState = viewModel.searchState.value
        assertEquals(SearchScreenState.Success(cities), searchState)
    }

    @Test
    fun `searchCity sets searchState to Loading and Empty when no cities are returned`() = testScope.runTest {
        // Arrange
        val emptyResult = Result.success(mutableListOf<Location>())

        coEvery { searchUseCase.getSearchResults("Nowhere") } returns flowOf(emptyResult)

        // Act
        viewModel.searchCity("Nowhere")
        advanceUntilIdle()

        // Assert
        val searchState = viewModel.searchState.value
        assertEquals(SearchScreenState.Empty, searchState)
    }

    @Test
    fun `fetchWeatherInfo sets weatherState to Loading and Success when weather info is fetched`() = testScope.runTest {
        // Arrange
        val cities = mutableListOf(
            Location("New Zealand", -36.8485, 174.7633, "Auckland", "Auckland")
        )
        val weatherInfo = WeatherInfo(
            current = mockk(),
            location = mockk()
        )
        val successWeatherResult = Result.success(weatherInfo)

        coEvery { searchUseCase.getSearchResults("Auckland") } returns flowOf(Result.success(cities))
        coEvery { weatherInfoUseCase.getWeatherInfo("Auckland") } returns flowOf(successWeatherResult)

        // Act
        viewModel.searchCity("Auckland")
        advanceUntilIdle()

        // Assert
        val weatherState = viewModel.weatherState.value
        assertEquals(WeatherScreenState.Success(weatherInfo), weatherState)
    }

    @Test
    fun `fetchWeatherInfo sets weatherState to Error when weather fetching fails`() = testScope.runTest {
        // Arrange
        val errorMessage = "Weather fetch failed"
        val errorWeatherResult = Result.error<WeatherInfo>(errorMessage)
        val cities = mutableListOf(
            Location("New Zealand", -36.8485, 174.7633, "Auckland", "Auckland")
        )

        coEvery { searchUseCase.getSearchResults("Auckland") } returns flowOf(Result.success(cities))
        coEvery { weatherInfoUseCase.getWeatherInfo("Auckland") } returns flowOf(errorWeatherResult)

        // Act
        viewModel.searchCity("Auckland")
        advanceUntilIdle()

        // Assert
        val weatherState = viewModel.weatherState.value
        assertEquals(WeatherScreenState.Error(errorMessage), weatherState)
    }

    @Test
    fun `fetchWeatherInfo sets weatherState to Empty when no weather data is returned`() = testScope.runTest {
        // Arrange
        val emptyWeatherResult = Result.empty<WeatherInfo>("", "")
        val cities = mutableListOf(
            Location("New Zealand", -36.8485, 174.7633, "Auckland", "Auckland")
        )

        coEvery { searchUseCase.getSearchResults("Auckland") } returns flowOf(Result.success(cities))
        coEvery { weatherInfoUseCase.getWeatherInfo("Auckland") } returns flowOf(emptyWeatherResult)

        // Act
        viewModel.searchCity("Auckland")
        advanceUntilIdle()

        // Assert
        val weatherState = viewModel.weatherState.value
        assertEquals(WeatherScreenState.Empty("", ""), weatherState)
    }
}