package eg.gov.iti.jets.kotlin.weather.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.kotlin.weather.LATITUDE
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentHomeBinding
import eg.gov.iti.jets.kotlin.weather.db.LocalSource
import eg.gov.iti.jets.kotlin.weather.home.viewmodel.HomeViewModel
import eg.gov.iti.jets.kotlin.weather.home.viewmodel.HomeViewModelFactory
import eg.gov.iti.jets.kotlin.weather.model.Repository
import eg.gov.iti.jets.kotlin.weather.network.APIState
import eg.gov.iti.jets.kotlin.weather.network.DayClient
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter
import kotlin.math.ceil

class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"
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
        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        daysAdapter = DaysAdapter()
        hoursAdapter = HoursAdapter()
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
                        Log.d(TAG, "onCreateView: done ${result.oneCall.current.weather.get(0)}")
                        binding.progressBar.visibility = View.GONE
                        binding.homeConstraintLayout.visibility = View.VISIBLE
                        binding.cityNameTextView.text = result.oneCall.timezone
                        binding.temperatureTextView.text =
                            ceil(result.oneCall.current.temp).toInt().toString()
                        binding.descriptionTextView.text =
                            result.oneCall.current.weather.get(0).description
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
                                    result.oneCall.current.weather.get(
                                        0
                                    ).icon
                                }@2x.png"
                            )
                            .into(binding.dayIconImageView)
                        binding.dateTextView.text = SimpleDateFormat("yyyy-MM-dd").format(
                            Date(result.oneCall.current.dt * 1000)
                        ).toString()
                        binding.timeTextView.text = SimpleDateFormat("HH:MM").format(
                            Date(result.oneCall.current.dt * 1000)
                        ).toString()
                        daysAdapter.submitList(result.oneCall.daily)
                        hoursAdapter.submitList(result.oneCall.hourly)
                        binding.cloudValueTextView.text = "${result.oneCall.current.clouds}%"
                        binding.windValueTextView.text =
                            result.oneCall.current.wind_speed.toString()
                        binding.pressureValueTextView.text =
                            result.oneCall.current.pressure.toString()
                        binding.humidityValueTextView.text =
                            result.oneCall.current.humidity.toString()
                        binding.visibilityValueTextView.text =
                            result.oneCall.current.visibility.toString()


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