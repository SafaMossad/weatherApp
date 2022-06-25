package com.wecancity.robusta_weather_app.models.currentWeather

import com.squareup.moshi.Json

data class WindDataModel(
    @field:Json(name = "deg") val   deg: Int,
    @field:Json(name = "gust") val   gust: Double,
    @field:Json(name = "speed") val   speed: Double
)