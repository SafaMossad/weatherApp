package com.wecancity.robusta_weather_app.networking

import com.wecancity.robusta_weather_app.models.currentWeather.CurrentWeatherModel
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteApiService {

    @GET("/data/2.5/weather")
    suspend fun weatherInformationService(
        @Query("lat") lat: Double?,
        @Query("lon") long: Double?,
        @Query("appid") appid: String?,
    ): CurrentWeatherModel



}