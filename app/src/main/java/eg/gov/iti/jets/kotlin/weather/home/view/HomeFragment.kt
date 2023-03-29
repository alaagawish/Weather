package eg.gov.iti.jets.kotlin.weather.home.view

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.kotlin.weather.*
import eg.gov.iti.jets.kotlin.weather.Constants.LANGUAGE
import eg.gov.iti.jets.kotlin.weather.Constants.LATITUDE
import eg.gov.iti.jets.kotlin.weather.Constants.LONGITUDE
import eg.gov.iti.jets.kotlin.weather.Constants.STRLOCATION
import eg.gov.iti.jets.kotlin.weather.Constants.TAG
import eg.gov.iti.jets.kotlin.weather.Constants.UNIT
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentHomeBinding
import eg.gov.iti.jets.kotlin.weather.db.LocalSource
import eg.gov.iti.jets.kotlin.weather.home.viewmodel.HomeViewModel
import eg.gov.iti.jets.kotlin.weather.home.viewmodel.HomeViewModelFactory
import eg.gov.iti.jets.kotlin.weather.model.*
import eg.gov.iti.jets.kotlin.weather.network.APIState
import eg.gov.iti.jets.kotlin.weather.network.DayClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

var units: Triple<String, String?, String?> = Triple("℃", "m/s", "Km")

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
        val locale = sharedPreferences.getString(LANGUAGE, "en")?.let { Locale(it) }
        if (locale != null) {
            Locale.setDefault(locale)
        }
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
        units = when (sharedPreferences.getString(UNIT, "metric")) {
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

        lifecycleScope.launch {
            if (isOnline(requireContext())) {
                println("remote source")
                homeViewModel.getForecastData(
                    sharedPreferences.getString(LATITUDE, "1.0")?.toDouble()!!,
                    sharedPreferences.getString(LONGITUDE, "1.0")?.toDouble()!!,
                    sharedPreferences.getString(UNIT, "metric")!!,
                    sharedPreferences.getString(
                        LANGUAGE, "en"
                    )!!
                )
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
                            binding.connectionAnimation.visibility = View.GONE
                            binding.homeConstraintLayout.visibility = View.VISIBLE

                            binding.cityNameTextView.text =
                                sharedPreferences.getString(STRLOCATION, result.oneCall.timezone)
                            binding.temperatureTextView.text =
                                "${ceil(result.oneCall.current.temp).toInt()}${units.first}"
                            binding.descriptionTextView.text =
                                result.oneCall.current.weather[0].description
                            binding.highLowTemperatureTextView.text = "Sunrise: ${
                                getDateFormat(
                                    "HH:MM",
                                    result.oneCall.current.sunrise
                                )
                            }\nSunset: ${
                                getDateFormat(
                                    "HH:MM", result.oneCall.current.sunset
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
                            binding.dateTextView.text =
                                getDateFormat("dd-MM-yyyy", result.oneCall.current.dt).toString()
                            binding.timeTextView.text =
                                getDateFormat("HH:MM", result.oneCall.current.dt)
                                    .toString()
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
                                requireActivity().findViewById(android.R.id.content),
                                "Something went wrong, check again later",
                                Snackbar.LENGTH_LONG
                            ).show()

                        }
                    }

                }

            } else if (sharedPreferences.getBoolean("isSavedLocal", false)) {
                println("local source")
                homeViewModel.comingDaysLocalStateFlow.collectLatest { result ->
                    when (result) {
                        is APIState.SuccessRoomDaily -> {
                            binding.progressBar.visibility = View.GONE
                            binding.homeConstraintLayout.visibility = View.VISIBLE
                            var list: MutableList<Daily> = mutableListOf<Daily>()
                            for (i in result.list) {
                                list.add(
                                    Daily(
                                        i.dt,
                                        Temp(i.min, i.max),
                                        mutableListOf(WeatherObject(i.main, i.description, i.icon))
                                    )
                                )
                            }
                            daysAdapter.submitList(list)
                            homeViewModel.hoursLocalStateFlow.collectLatest { result ->
                                when (result) {
                                    is APIState.SuccessRoomHours -> {
                                        binding.progressBar.visibility = View.GONE
                                        binding.homeConstraintLayout.visibility = View.VISIBLE

                                        var list: MutableList<Hourly> = mutableListOf<Hourly>()
                                        for (i in result.list) {
                                            list.add(
                                                Hourly(
                                                    i.dt,
                                                    i.temp,
                                                    mutableListOf(
                                                        WeatherObject(
                                                            i.main,
                                                            i.description,
                                                            i.icon
                                                        )
                                                    )
                                                )
                                            )
                                        }
                                        hoursAdapter.submitList(list)
                                        homeViewModel.dayLocalStateFlow.collectLatest { result ->
                                            when (result) {
                                                is APIState.SuccessRoomDay -> {
                                                    binding.progressBar.visibility = View.GONE
                                                    binding.homeConstraintLayout.visibility =
                                                        View.VISIBLE

                                                    binding.cityNameTextView.text =
                                                        result.day.timezone
                                                    binding.temperatureTextView.text =
                                                        "${ceil(result.day.temp).toInt()}${units.first}"
                                                    binding.descriptionTextView.text =
                                                        result.day.description
                                                    binding.highLowTemperatureTextView.text =
                                                        "Sunrise: ${
                                                            getDateFormat(
                                                                "HH:MM",
                                                                result.day.sunrise
                                                            )
                                                        }\nSunset: ${
                                                            getDateFormat(
                                                                "HH:MM",
                                                                result.day.sunset
                                                            )
                                                        }"
                                                    Picasso
                                                        .get()
                                                        .load(
                                                            "https://openweathermap.org/img/wn/${
                                                                result.day.icon
                                                            }@2x.png"
                                                        )
                                                        .into(binding.dayIconImageView)
                                                    binding.dateTextView.text = getDateFormat(
                                                        "dd-MM-yyyy",
                                                        result.day.dt
                                                    ).toString()
                                                    binding.timeTextView.text = getDateFormat(
                                                        "HH:MM",
                                                        result.day.dt
                                                    ).toString()
                                                    binding.cloudValueTextView.text =
                                                        "${result.day.clouds}%"
                                                    binding.windValueTextView.text =
                                                        "${result.day.wind_speed}${units.second}"
                                                    binding.pressureValueTextView.text =
                                                        "${result.day.pressure}hpa"
                                                    binding.humidityValueTextView.text =
                                                        "${result.day.humidity}%"
                                                    binding.visibilityValueTextView.text =
                                                        "${result.day.visibility / 1000}${units.third}"
                                                }
                                                is APIState.Waiting -> {
                                                    binding.homeConstraintLayout.visibility =
                                                        View.GONE
                                                    binding.progressBar.visibility = View.VISIBLE
                                                }
                                                else -> {
                                                    binding.progressBar.visibility = View.GONE
                                                    binding.homeConstraintLayout.visibility =
                                                        View.GONE
                                                    Snackbar.make(
                                                        requireActivity().findViewById(android.R.id.content),
                                                        "Something went wrong, check again later",
                                                        Snackbar.LENGTH_LONG
                                                    ).show()

                                                }
                                            }

                                        }

                                    }
                                    is APIState.Waiting -> {
                                        binding.homeConstraintLayout.visibility = View.GONE
                                        binding.progressBar.visibility = View.VISIBLE
                                    }
                                    else -> {
                                        binding.progressBar.visibility = View.GONE
                                        binding.homeConstraintLayout.visibility = View.GONE
                                        Snackbar.make(
                                            requireActivity().findViewById(android.R.id.content),
                                            "Something went wrong, check again later",
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                    }
                                }


                            }


                        }
                        is APIState.Waiting -> {
                            binding.homeConstraintLayout.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        else -> {

                            binding.progressBar.visibility = View.GONE
                            binding.homeConstraintLayout.visibility = View.GONE
                            Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                "Something went wrong, check again later",
                                Snackbar.LENGTH_LONG
                            ).show()
                            Log.d(TAG, "onViewCreated: $result")
                        }
                    }
                }

            } else {
                binding.homeConstraintLayout.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.connectionAnimation.visibility = View.VISIBLE
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Check your connection, please",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
            }
        }
        return false
    }

    private fun getDateFormat(pattern: String, date: Long) =
        SimpleDateFormat(pattern).format(Date(date * 1000))
}