package com.mobiquity.challenge.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    private val weatherForecast: MutableLiveData<WeatherForecast> = MutableLiveData()
    val text: LiveData<String> = _text

    init {
        weatherForecast.value = WeatherForecast()
    }

    data class WeatherForecast(
        val coord: Coord? = null,
        val weather: ArrayList<Weather>? = null,
        val base: String? = null,
        val main: Main? = null,
        val timeZone: String? = null,
        val id: String? = null,
        val name: String? = null,
        val cod: String? = null,
        val visibility: String? = null,
        val wind: Wind? = null,
        val dt: Long? = null,
        val sys: Sys? = null
    )

    class Coord(
        val lat: Float,
        val lon: Float
    )

    class Weather(
        val id: String,
        val main: String,
        val description: String,
        val icon: String
    )

    class Main(
        val temp: Float,
        val feels_like: Float,
        val temp_min: Float,
        val temp_max: Float,
        val pressure: Float,
        val humidity: Float
    )

    class Wind(
        val speed: Float,
        val deg: Float
    )

    class Sys(
        val type: Int,
        val id: Int,
        val country: String,
        val sunrise: Long,
        val sunset: Long
    )
}