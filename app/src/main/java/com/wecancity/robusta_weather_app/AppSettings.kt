package com.wecancity.robusta_weather_app
import android.app.Application
import buildApiService


class AppSettings : Application() {

  companion object {
    private lateinit var instance: AppSettings

    //call buildApiService from retrofit builder to build retrofit
     val apiService by lazy { buildApiService() }


  }
  override fun onCreate() {
    super.onCreate()
    instance = this
  }
}