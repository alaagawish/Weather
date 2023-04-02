package eg.gov.iti.jets.kotlin.weather.favourite.view


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.kotlin.weather.*
import eg.gov.iti.jets.kotlin.weather.utils.Constants.SOURCE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.TAG
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentFavouriteBinding
import eg.gov.iti.jets.kotlin.weather.db.DayDatabase
import eg.gov.iti.jets.kotlin.weather.db.LocalSource
import eg.gov.iti.jets.kotlin.weather.favourite.viewmodel.FavouriteViewModel
import eg.gov.iti.jets.kotlin.weather.favourite.viewmodel.FavouriteViewModelFactory
import eg.gov.iti.jets.kotlin.weather.home.view.DaysAdapter
import eg.gov.iti.jets.kotlin.weather.home.view.HoursAdapter
import eg.gov.iti.jets.kotlin.weather.home.view.units
import eg.gov.iti.jets.kotlin.weather.viewmodel.HomeViewModel
import eg.gov.iti.jets.kotlin.weather.viewmodel.HomeViewModelFactory
import eg.gov.iti.jets.kotlin.weather.map.MapsActivity
import eg.gov.iti.jets.kotlin.weather.model.*
import eg.gov.iti.jets.kotlin.weather.model.APIState
import eg.gov.iti.jets.kotlin.weather.network.DayClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

class FavouriteFragment : Fragment(), PlaceOnClickListener {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var favouriteViewModelFactory: FavouriteViewModelFactory
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var placesAdapter: PlacesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("LogNotTimber")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favouriteViewModelFactory = FavouriteViewModelFactory(
            Repository.getInstance(
                DayClient.getInstance(),
                LocalSource(
                    DayDatabase.getInstance(requireContext()).getFavDao(),
                    DayDatabase.getInstance(requireContext()).getDayDao(),
                    DayDatabase.getInstance(requireContext()).getAlertsDao(),
                    DayDatabase.getInstance(requireContext()).getHourDao(),
                    DayDatabase.getInstance(requireContext()).getDailyDao()
                )
            )
        )

        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                DayClient.getInstance(),
                LocalSource(
                    DayDatabase.getInstance(requireContext()).getFavDao(),
                    DayDatabase.getInstance(requireContext()).getDayDao(),
                    DayDatabase.getInstance(requireContext()).getAlertsDao(),
                    DayDatabase.getInstance(requireContext()).getHourDao(),
                    DayDatabase.getInstance(requireContext()).getDailyDao()
                )
            )
        )
        favouriteViewModel =
            ViewModelProvider(this, favouriteViewModelFactory)[FavouriteViewModel::class.java]

        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        placesAdapter = PlacesAdapter(this)

        binding.addCityFloatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), MapsActivity::class.java)
            intent.putExtra(SOURCE, "fav")
            startActivity(intent)
        }
        binding.favouritesRecyclerView.adapter = placesAdapter

        lifecycleScope.launch {
            favouriteViewModel.favLocalPlacesStateFlow.collectLatest { result ->
                when (result) {
                    is APIState.Failure -> {
                        binding.favProgressBar.visibility = View.GONE
                        binding.favouritesRecyclerView.visibility = View.GONE
                        binding.noPlacesImageView.visibility = View.GONE
                        binding.noPlacesTextView.visibility = View.GONE
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "Something went wrong, check again later",
                            Snackbar.LENGTH_LONG
                        ).show()
                        Log.d(
                            TAG,
                            "onViewCreated: error in retrieving list of fav from local source $result"
                        )
                    }
                    is APIState.SuccessFavPlaces -> {
                        if (result.list.isNotEmpty()) {
                            placesAdapter.submitList(result.list)
                            binding.favProgressBar.visibility = View.GONE
                            binding.favouritesRecyclerView.visibility = View.VISIBLE
                            binding.noPlacesImageView.visibility = View.GONE
                            binding.noPlacesTextView.visibility = View.GONE
                        } else {
                            binding.noPlacesImageView.visibility = View.VISIBLE
                            binding.noPlacesTextView.visibility = View.VISIBLE
                            binding.favProgressBar.visibility = View.GONE
                            binding.favouritesRecyclerView.visibility = View.GONE


                        }
                    }
                    else -> {
                        binding.favProgressBar.visibility = View.VISIBLE
                        binding.favouritesRecyclerView.visibility = View.GONE
                        binding.noPlacesImageView.visibility = View.GONE
                        binding.noPlacesTextView.visibility = View.GONE

                    }
                }
            }
        }

        binding.backToFav.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.navController.navigate(R.id.favouriteFragment)
        }
    }

    override fun displayPlace(favouritePlace: FavouritePlace) {

        homeViewModel.getForecastData(favouritePlace.lat, favouritePlace.lon)
        lifecycleScope.launch {
            homeViewModel.forecastStateFlow.collectLatest { result ->
                when (result) {
                    is APIState.Waiting -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.homeConstraintLayout.visibility = View.GONE
                        Log.d(TAG, "onCreateView: waiting")

                    }
                    is APIState.Success -> {
                        val daysAdapter = DaysAdapter(requireContext())
                        val hoursAdapter = HoursAdapter(requireContext())
                        binding.daysDetailsRecyclerView.adapter = daysAdapter
                        binding.hoursDetailsRecyclerView.adapter = hoursAdapter
                        Log.d(TAG, "onCreateView: done ${result.oneCall.current.weather[0]}")
                        binding.progressBar.visibility = View.GONE
                        binding.homeConstraintLayout.visibility = View.VISIBLE
                        binding.connectionAnimation.visibility = View.GONE
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
                            requireActivity().findViewById(android.R.id.content),
                            "Something went wrong, check again later",
                            Snackbar.LENGTH_LONG
                        ).show()

                    }
                }

            }
        }
        changeLayout()
    }

    override fun deletePlace(favouritePlace: FavouritePlace) {
        val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
        builder.setTitle(context?.getString(R.string.delete_question))
        builder.setMessage(context?.getString(R.string.are_you_sure_to_delete))
        builder.setIcon(R.drawable.baseline_delete_24)
        builder.setPositiveButton(context?.getString(R.string.yes)) { _, _ ->
            favouriteViewModel.deletePlaceFromFav(favouritePlace)
        }

        builder.setNegativeButton(context?.getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()

    }

    private fun changeLayout() {
        binding.favProgressBar.visibility = View.GONE
        binding.addCityFloatingActionButton.visibility = View.GONE
        binding.noPlacesImageView.visibility = View.GONE
        binding.noPlacesTextView.visibility = View.GONE
        binding.favouritesRecyclerView.visibility = View.GONE
        binding.displayLayout.visibility = View.VISIBLE

    }


}