package com.mobiquity.challenge

import com.google.android.gms.maps.model.LatLng

class Constant {

    companion object {
        private const val appId = "fae7190d7e6433ec3a45285ffcf55c86"

        fun getForecastUrl(latLng: LatLng): String {
            return "http://api.openweathermap.org/data/2.5/forecast?lat=${latLng.latitude}&lon=" +
                    "${latLng.longitude}&appid=$appId&units=metric"
        }

        fun getWeatherUrl(latLng: LatLng): String {
            return "http://api.openweathermap.org/data/2.5/weather?lat=${latLng.latitude}&lon=" +
                    "${latLng.longitude}&appid=$appId&units=metric"
        }
    }
}