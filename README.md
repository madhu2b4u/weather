# Weather App

This weather app demonstrates the use of **Kotlin**, **Jetpack Compose**, and **Clean Architecture** to fetch and display weather information for a selected city. The app allows users to search for a city, view its weather data, and persist the selected city across app launches.

The weather data is fetched from **WeatherAPI.com**, and the UI is designed based on **Figma designs**.

---

## **Objective**

The goal of this app is to:
- Search for a city.
- Display its weather details on the home screen.
- Persist the selected city using local storage so it is visible on subsequent app launches.

---

## **Features**

### 1. **Home Screen**:
   - Displays weather information for a saved city:
     - City name.
     - Temperature.
     - Weather condition (with an icon from the API).
     - Humidity (%).
     - UV index.
     - "Feels like" temperature.
   - If no city is saved, the app prompts the user to search for a city.
   - Includes a **search bar** for querying new cities.

### 2. **Search Behavior**:
   - Show a **search result card** with weather information for the queried city.
   - Tapping a result updates the Home Screen with the city's weather and persists the selection.

### 3. **Local Storage**:
   - Use **SharedPreferences** or **DataStore** to persist the selected city.
   - The app reloads the selected city's weather on each launch.

---

## **Technologies Used**

- **Kotlin**
- **Jetpack Compose**
- **Clean Architecture**
- **SharedPreferences/DataStore** for local storage
- **WeatherAPI.com** for fetching weather data
- **Hilt** for Dependency Injection
- **Retrofit** for networking
- **Room** for database storage

---

## **Getting Started**

To set up the project locally, follow the steps below:

### 1. **Clone the repository**

```bash
git clone https://github.com/yourusername/weather-app.git
cd weather-app
```

### 2. **Prerequisites**

Ensure you have the following installed on your machine:
- **Android Studio** (latest stable version)
- **JDK 11** or later
- **Gradle** (comes with Android Studio)

### 3. **Set up API Key for WeatherAPI**

To interact with the **WeatherAPI.com** service, you'll need to sign up and get an API key.

- Visit [WeatherAPI.com](https://weatherapi.com) and create an account.
- Get your API key from your dashboard.

### 4. **Sync Gradle**

Open the project in **Android Studio** and sync Gradle by clicking **Sync Now** in the top bar, or run the following command in the terminal:

```bash
./gradlew sync
```

### 5. **Run the App**

- In Android Studio, select a **device** or **emulator** and click the **Run** button (green play icon) or run the following in the terminal:

```bash
./gradlew installDebug
```

This will launch the app on your chosen device/emulator.

---

- **data**: Handles network calls, local storage, and models for data.
- **domain**: Contains business logic and use cases.
- **presentation**: Handles UI components, screens, and ViewModel logic.
- **di**: Contains the Hilt DI setup.

---

## **Testing**

You can run unit and UI tests by using the following commands:

- **Unit Tests**: Run tests with Gradle:

  ```bash
  ./gradlew test
  ```

- **UI Tests**: Run UI tests using Android Studio or via the terminal:

  ```bash
  ./gradlew connectedAndroidTest
 ```
