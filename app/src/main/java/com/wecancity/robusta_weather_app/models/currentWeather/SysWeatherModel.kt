package com.wecancity.robusta_weather_app.models.currentWeather
import com.squareup.moshi.Json

data class SysWeatherModel(
    @field:Json(name = "country") val  country: String,
    @field:Json(name = "id") val  id: Int,
    @field:Json(name = "sunrise") val  sunrise: Int,
    @field:Json(name = "sunset") val  sunset: Int,
    @field:Json(name = "type") val  type: Int
)