package com.wecancity.robusta_weather_app.repository
import com.wecancity.motem.models.Failure
import com.wecancity.motem.models.Result
import com.wecancity.motem.models.Success
import com.wecancity.robusta_weather_app.AppSettings.Companion.apiService
import com.wecancity.robusta_weather_app.models.currentWeather.CurrentWeatherModel
import com.wecancity.robusta_weather_app.viewUtils.API_KEY

class WeatherInformationRepository {

    suspend fun homeCategories(lat:Double,long:Double): Result<CurrentWeatherModel> = try {
        val data = apiService.weatherInformationService(lat,long,API_KEY)
        Success(data)
    } catch (error: Throwable) {
        Failure(error)
    }
}