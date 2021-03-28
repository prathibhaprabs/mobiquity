package com.mobiquity.challenge.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.mobiquity.challenge.Constant
import com.mobiquity.challenge.R
import com.mobiquity.challenge.helper.ApiCall

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        getTodayWeatherReport()
        return root
    }

    private fun getTodayWeatherReport() {
        val sydney = LatLng(-34.2, 151.2)
        ApiCall.get(activity, Constant.getUrl(sydney), object : ApiCall.ApiResponse {
            override fun onApiSuccess(response: String?) {
                if (activity == null) return
                Toast.makeText(activity, "API success", Toast.LENGTH_SHORT).show()
                // TODO: 29/3/21 convert it to pojo and show values
            }

            override fun onApiFailure() {
                if (activity == null) return
                Toast.makeText(activity, "API fail", Toast.LENGTH_SHORT).show()
            }
        })
    }
}