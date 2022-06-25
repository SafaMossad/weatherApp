package com.wecancity.robusta_weather_app.models.currentWeather

import com.squareup.moshi.Json

data class CurrentWeatherModel(
    @field:Json(name = "base") val  base: String,
    @field:Json(name = "clouds") val  clouds: CloudsModel,
    @field:Json(name = "cod") val  cod: Int,
    @field:Json(name = "coord") val  coord: CoordWeatherModel,
    @field:Json(name = "dt") val  dt: Int,
    @field:Json(name = "id") val  id: Int,
    @field:Json(name = "main") val  main: MainWeatherModel,
    @field:Json(name = "name") val  name: String,
    @field:Json(name = "sys") val  sys: SysWeatherModel,
    @field:Json(name = "timezone") val  timezone: Int,
    @field:Json(name = "visibility") val  visibility: Int,
    @field:Json(name = "weather") val  weather: List<WeatherDataModel>,
    @field:Json(name = "wind") val  wind: WindDataModel
)