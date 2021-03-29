package com.mobiquity.challenge.ui.city

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobiquity.challenge.Constant
import com.mobiquity.challenge.R
import com.mobiquity.challenge.helper.ApiCall
import mumayank.com.airrecyclerview.AirRv
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class CityFragment(position: LatLng) : Fragment() {

    private lateinit var cityViewModel: CityViewModel
    private lateinit var temperatureTv: TextView
    private lateinit var humidityTv: TextView
    private lateinit var windTv: TextView
    private lateinit var rainTv: TextView
    private lateinit var lastTenDaysWeatherRv: LinearLayout
    private lateinit var progressBar: LinearLayout
    private val latLng = position
    private var weatherInfoReceived = false
    private var weatherForecastReceived = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cityViewModel = ViewModelProvider(this).get(CityViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_city, container, false)

        temperatureTv = root.findViewById(R.id.temperatureTv)
        humidityTv = root.findViewById(R.id.humidityTv)
        windTv = root.findViewById(R.id.windTv)
        rainTv = root.findViewById(R.id.rainTv)
        lastTenDaysWeatherRv = root.findViewById(R.id.lastTenDaysWeatherRv)
        progressBar = root.findViewById(R.id.progressBar)

        getTodayWeatherReport()
        getWeatherForecastReport()
        return root
    }

    private fun getTodayWeatherReport() {
        ApiCall.get(activity, Constant.getWeatherUrl(latLng), object : ApiCall.ApiResponse {
            override fun onApiSuccess(response: String?) {
                if (activity == null) return
                val weatherInfo: CityViewModel.WeatherInfo = Gson().fromJson(
                    response, object : TypeToken<CityViewModel.WeatherInfo>() {}.type
                )
                val temp = "${weatherInfo.main?.temp} " +
                        "(min - ${weatherInfo.main?.temp_min}, max - ${weatherInfo.main?.temp_max})"

                temperatureTv.text = temp
                humidityTv.text = weatherInfo.main?.humidity
                windTv.text = weatherInfo.wind?.speed
                rainTv.text = weatherInfo.weather?.get(0)?.description
                weatherInfoReceived = true
                hideProgressBar()
            }

            override fun onApiFailure() {
                if (activity == null) return
                Toast.makeText(activity, "Weather report API failed", Toast.LENGTH_SHORT).show()
                weatherInfoReceived = true
                hideProgressBar()
            }
        })
    }

    private fun getWeatherForecastReport() {
        ApiCall.get(activity, Constant.getForecastUrl(latLng), object : ApiCall.ApiResponse {
            override fun onApiSuccess(response: String?) {
                if (activity == null) return
                val weatherForecast: CityViewModel.WeatherForecast = Gson().fromJson(
                    response, object : TypeToken<CityViewModel.WeatherForecast>() {}.type
                )

                val map: HashMap<String, CityViewModel.WeatherInfo> = HashMap()
                val weatherForecastList: ArrayList<CityViewModel.WeatherInfo> = arrayListOf()

                weatherForecast.list.forEach {
                    val date = SimpleDateFormat(
                        "E, dd MMM", Locale.getDefault()
                    ).format(it.dt?.times(1000))

                    if (map.containsKey(date).not()) {
                        weatherForecastList.add(it)
                        map[date] = it
                    }
                }

                setRv(weatherForecastList)
                weatherForecastReceived = true
                hideProgressBar()
            }

            override fun onApiFailure() {
                if (activity == null) return
                Toast.makeText(activity, "Weather forecast API failed!!", Toast.LENGTH_SHORT).show()
                weatherForecastReceived = true
                hideProgressBar()
            }
        })
    }

    private fun setRv(weatherInfoList: ArrayList<CityViewModel.WeatherInfo>) {
        AirRv(object : AirRv.Callback {
            override fun aGetAppContext(): Context? {
                return activity
            }

            override fun bGetLayoutManager(appContext: Context?): RecyclerView.LayoutManager? {
                return LinearLayoutManager(appContext)
            }

            override fun cGetRvHolderViewGroup(): ViewGroup? {
                return lastTenDaysWeatherRv
            }

            override fun dGetSize(): Int? {
                return weatherInfoList.size
            }

            override fun eGetViewType(position: Int): Int? {
                return 0
            }

            override fun fGetViewLayoutId(parent: ViewGroup, viewType: Int): Int? {
                return R.layout.weather_rv_list_item
            }

            override fun gGetViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
                return CustomViewHolder(view)
            }

            override fun hGetBindView(
                viewHolder: RecyclerView.ViewHolder, viewType: Int, position: Int
            ) {
                val customViewHolder = viewHolder as CustomViewHolder
                val weatherForecast = weatherInfoList[position]

                customViewHolder.descriptionTv.text = weatherForecast.weather?.get(0)?.description
                customViewHolder.dateTv.text =
                    SimpleDateFormat("E, dd MMM", Locale.getDefault())
                        .format(weatherForecast.dt?.times(1000))
                customViewHolder.minTempTv.text = weatherForecast.main?.temp_min
                customViewHolder.maxTempTv.text = weatherForecast.main?.temp_max
            }
        })
    }

    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTv: TextView = view.findViewById(R.id.dateTv)
        val maxTempTv: TextView = view.findViewById(R.id.maxTempTv)
        val minTempTv: TextView = view.findViewById(R.id.minTempTv)
        val descriptionTv: TextView = view.findViewById(R.id.descriptionTv)
    }

    private fun hideProgressBar() {
        if (weatherInfoReceived && weatherForecastReceived)
            progressBar.visibility = View.GONE
    }
}