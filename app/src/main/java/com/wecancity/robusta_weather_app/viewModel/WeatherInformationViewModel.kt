package com.wecancity.robusta_weather_app.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wecancity.motem.models.Success
import com.wecancity.robusta_weather_app.models.currentWeather.CurrentWeatherModel
import com.wecancity.robusta_weather_app.repository.WeatherInformationRepository
import kotlinx.coroutines.launch

class WeatherInformationViewModel(private val weatherInformationRepo: WeatherInformationRepository) :
    ViewModel() {
    var weatherInformationLiveData: MutableLiveData<CurrentWeatherModel> = MutableLiveData()

    fun getWeatherInformation(lat: Double, long: Double) = viewModelScope.launch {
        val result = weatherInformationRepo.homeCategories(lat, long)
        if (result is Success) {
            weatherInformationLiveData.postValue(result.data)
        } else {
            Log.i("WeatherInformation",
                "WeatherInformation: Some Thing error while loading whether data")
        }
    }
}