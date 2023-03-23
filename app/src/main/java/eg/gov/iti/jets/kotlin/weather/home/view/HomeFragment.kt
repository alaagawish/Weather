package eg.gov.iti.jets.kotlin.weather.home.view

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.kotlin.weather.LANGUAGE
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.UNIT
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentHomeBinding
import eg.gov.iti.jets.kotlin.weather.db.LocalSource
import eg.gov.iti.jets.kotlin.weather.home.viewmodel.HomeViewModel
import eg.gov.iti.jets.kotlin.weather.home.viewmodel.HomeViewModelFactory
import eg.gov.iti.jets.kotlin.weather.model.*
import eg.gov.iti.jets.kotlin.weather.network.APIState
import eg.gov.iti.jets.kotlin.weather.network.DayClient
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var hoursAdapter: HoursAdapter
    private lateinit var daysAdapter: DaysAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentHomeBinding.inflate(inflater, container, false)
        val locale = Locale(sharedPreferences.getString(LANGUAGE, "en"))
        Locale.setDefault(locale)
        val res: Resources = context?.resources!!
        val configuration = Configuration(res.configuration)
        configuration.locale = locale
        res.updateConfiguration(configuration, res.displayMetrics)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                DayClient.getInstance(),
                LocalSource(requireContext())
            )
        )
        val units = when (sharedPreferences.getString(UNIT, "metric")) {
            "metric" -> Triple(
                "℃",
                context?.getString(R.string.m_sec),
                context?.getString(R.string.kilo_meter)
            )
            "imperial" -> Triple(
                "℉",
                context?.getString(R.string.m_hour),
                context?.getString(R.string.yard)
            )
            else -> Triple(
                "K",
                context?.getString(R.string.m_sec),
                context?.getString(R.string.kilo_meter)
            )
        }
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        daysAdapter = DaysAdapter(requireContext())
        hoursAdapter = HoursAdapter(requireContext())
        binding.daysDetailsRecyclerView.adapter = daysAdapter
        binding.hoursDetailsRecyclerView.adapter = hoursAdapter
//        if (sharedPreferences.getString(LATITUDE, "1.0")?.toDouble() == 1.0)
//            Snackbar.make(requireContext(),view, "Turn on GPS", Snackbar.LENGTH_LONG).show()

        lifecycleScope.launch {

            homeViewModel.forecastStateFlow.collectLatest { result ->
                when (result) {
                    is APIState.Waiting -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.homeConstraintLayout.visibility = View.GONE
                        Log.d(TAG, "onCreateView: waiting")

                    }
                    is APIState.Success -> {
                        Log.d(TAG, "onCreateView: done ${result.oneCall.current.weather[0]}")
                        val day = DayDBModel(
                            result.oneCall.current.dt,
                            result.oneCall.lat,
                            result.oneCall.lon,
                            result.oneCall.timezone,
                            result.oneCall.current.sunrise,
                            result.oneCall.current.sunset,
                            result.oneCall.current.temp,
                            result.oneCall.current.pressure,
                            result.oneCall.current.humidity,
                            result.oneCall.current.uvi,
                            result.oneCall.current.clouds,
                            result.oneCall.current.visibility,
                            result.oneCall.current.wind_speed,
                            result.oneCall.current.weather[0].main,
                            result.oneCall.current.weather[0].description,
                            result.oneCall.current.weather[0].icon
                        )

                        val days = mutableListOf<DailyDBModel>()
                        for (i in result.oneCall.daily) {
                            days.add(
                                DailyDBModel(
                                    i.dt,
                                    i.temp.min,
                                    i.temp.max,
                                    i.weather[0].main,
                                    i.weather[0].description,
                                    i.weather[0].icon
                                )
                            )
                        }
                        val hours = mutableListOf<HourlyDBModel>()
                        for (i in result.oneCall.hourly) {
                            hours.add(
                                HourlyDBModel(
                                    i.dt,
                                    i.temp,
                                    i.weather[0].main,
                                    i.weather[0].description,
                                    i.weather[0].icon
                                )
                            )
                        }
                        homeViewModel.addDay(day, hours, days)
                        binding.progressBar.visibility = View.GONE
                        binding.homeConstraintLayout.visibility = View.VISIBLE
                        binding.cityNameTextView.text = result.oneCall.timezone
                        binding.temperatureTextView.text =
                            "${ceil(result.oneCall.current.temp).toInt()}${units.first}"
                        binding.descriptionTextView.text =
                            result.oneCall.current.weather[0].description
                        binding.highLowTemperatureTextView.text = "Sunrise: ${
                            SimpleDateFormat("HH:MM").format(
                                Date(result.oneCall.current.sunrise * 1000)
                            )
                        }\nSunset: ${
                            SimpleDateFormat("HH:MM").format(
                                Date(result.oneCall.current.sunset * 1000)
                            )
                        }"
                        Picasso
                            .get()
                            .load(
                                "https://openweathermap.org/img/wn/${
                                    result.oneCall.current.weather[0].icon
                                }@2x.png"
                            )
                            .into(binding.dayIconImageView)
                        binding.dateTextView.text = SimpleDateFormat("dd-MM-yyyy").format(
                            Date(result.oneCall.current.dt * 1000)
                        ).toString()
                        binding.timeTextView.text = SimpleDateFormat("HH:MM").format(
                            Date(result.oneCall.current.dt * 1000)
                        ).toString()
                        daysAdapter.submitList(result.oneCall.daily)

                        hoursAdapter.submitList(result.oneCall.hourly)
                        binding.cloudValueTextView.text = "${result.oneCall.current.clouds}%"
                        binding.windValueTextView.text =
                            "${result.oneCall.current.wind_speed}${units.second}"
                        binding.pressureValueTextView.text =
                            "${result.oneCall.current.pressure}hpa"
                        binding.humidityValueTextView.text =
                            "${result.oneCall.current.humidity}%"
                        binding.visibilityValueTextView.text =
                            "${result.oneCall.current.visibility / 1000}${units.third}"


                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.homeConstraintLayout.visibility = View.GONE
                        Snackbar.make(
                            binding.homeConstraintLayout,
                            "Please check connection",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Action", null).show()
                        Toast.makeText(
                            requireContext(),
                            "check connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}