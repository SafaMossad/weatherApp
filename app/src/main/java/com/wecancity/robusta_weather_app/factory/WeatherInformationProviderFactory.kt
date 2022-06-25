package com.wecancity.robusta_weather_app.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wecancity.robusta_weather_app.repository.WeatherInformationRepository
import com.wecancity.robusta_weather_app.viewModel.WeatherInformationViewModel


class WeatherInformationProviderFactory(
    private val weatherInformationRepository:
    WeatherInformationRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherInformationViewModel(weatherInformationRepository) as T
    }

}