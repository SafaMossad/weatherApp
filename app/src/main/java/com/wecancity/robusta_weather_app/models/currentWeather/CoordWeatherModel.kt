package com.wecancity.robusta_weather_app.models.currentWeather
import com.squareup.moshi.Json

data class CoordWeatherModel(
    @field:Json(name = "lat") val  lat: Double,
    @field:Json(name = "log") val  lon: Double
)