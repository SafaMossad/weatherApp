package com.wecancity.robusta_weather_app.models.currentWeather

import com.squareup.moshi.Json

data class CloudsModel(
    @field:Json(name = "all") val all: Int
)