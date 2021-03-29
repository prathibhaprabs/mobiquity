package com.mobiquity.challenge.ui.home

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

class HomeFragment(position: LatLng) : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var temperatureTv: TextView
    private lateinit var humidityTv: TextView
    private lateinit var windTv: TextView
    private lateinit var rainTv: TextView
    private lateinit var lastTenDaysWeatherRv: LinearLayout
    private val latLng = position

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        temperatureTv = root.findViewById(R.id.temperatureTv)
        humidityTv = root.findViewById(R.id.humidityTv)
        windTv = root.findViewById(R.id.windTv)
        rainTv = root.findViewById(R.id.rainTv)
        lastTenDaysWeatherRv = root.findViewById(R.id.lastTenDaysWeatherRv)

        getTodayWeatherReport()
        getTenDaysWeatherReport()
        return root
    }

    private fun getTodayWeatherReport() {
        ApiCall.get(activity, Constant.getWeatherUrl(latLng), object : ApiCall.ApiResponse {
            override fun onApiSuccess(response: String?) {
                if (activity == null) return
                Toast.makeText(activity, "API success", Toast.LENGTH_SHORT).show()
                val weatherForecast: HomeViewModel.WeatherForecast =
                    Gson().fromJson(
                        response, object : TypeToken<HomeViewModel.WeatherForecast>() {}.type
                    )

                temperatureTv.text = "${weatherForecast.main?.temp} " +
                        "(min - ${weatherForecast.main?.temp_min}, max - ${weatherForecast.main?.temp_max})"
                humidityTv.text = weatherForecast.main?.humidity
                windTv.text = weatherForecast.wind?.speed
                rainTv.text = weatherForecast.weather?.get(0)?.description
            }

            override fun onApiFailure() {
                if (activity == null) return
                Toast.makeText(activity, "API fail", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getTenDaysWeatherReport() {
        ApiCall.get(activity, Constant.getForecastUrl(latLng), object : ApiCall.ApiResponse {
            override fun onApiSuccess(response: String?) {
                if (activity == null) return
                Toast.makeText(activity, "API success", Toast.LENGTH_SHORT).show()
                val weatherForecastList: ArrayList<HomeViewModel.WeatherForecast> =
                    Gson().fromJson(
                        response,
                        object : TypeToken<ArrayList<HomeViewModel.WeatherForecast>>() {}.type
                    )
                setRv(weatherForecastList)
            }

            override fun onApiFailure() {
                if (activity == null) return
                Toast.makeText(activity, "API fail", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setRv(weatherForecastList: java.util.ArrayList<HomeViewModel.WeatherForecast>) {
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
                weatherForecastList.size
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
                viewHolder: RecyclerView.ViewHolder,
                viewType: Int,
                position: Int
            ) {
                val customViewHolder = viewHolder as CustomViewHolder
                val weatherForecast = weatherForecastList[position]


                customViewHolder.textView.text = "${weatherForecast.main?.temp} " +
                        "(min - ${weatherForecast.main?.temp_min}, max - ${weatherForecast.main?.temp_max})"
//                humidityTv.text = weatherForecast.main?.humidity
//                windTv.text = weatherForecast.wind?.speed
//                rainTv.text = weatherForecast.weather?.get(0)?.description

            }
        })
    }

    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.descriptionTv
    }
}