package com.wecancity.robusta_weather_app.models.currentWeather

import com.squareup.moshi.Json

data class WeatherDataModel(
    @field:Json(name = "description") val  description: String,
    @field:Json(name = "icon") val  icon: String,
    @field:Json(name = "id") val  id: Int,
    @field:Json(name = "main") val  main: String
)