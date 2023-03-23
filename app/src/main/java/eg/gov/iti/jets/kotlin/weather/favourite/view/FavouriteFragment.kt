package eg.gov.iti.jets.kotlin.weather.favourite.view

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
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentFavouriteBinding
import eg.gov.iti.jets.kotlin.weather.db.LocalSource
import eg.gov.iti.jets.kotlin.weather.favourite.viewmodel.FavouriteViewModel
import eg.gov.iti.jets.kotlin.weather.favourite.viewmodel.FavouriteViewModelFactory
import eg.gov.iti.jets.kotlin.weather.home.viewmodel.HomeViewModel
import eg.gov.iti.jets.kotlin.weather.home.viewmodel.HomeViewModelFactory
import eg.gov.iti.jets.kotlin.weather.model.*
import eg.gov.iti.jets.kotlin.weather.network.APIState
import eg.gov.iti.jets.kotlin.weather.network.DayClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment(), PlaceOnClickListener {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var favouriteViewModelFactory: FavouriteViewModelFactory
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var placesAdapter: PlacesAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favouriteViewModelFactory = FavouriteViewModelFactory(
            Repository.getInstance(
                DayClient.getInstance(),
                LocalSource(requireContext())
            )
        )

        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                DayClient.getInstance(),
                LocalSource(requireContext())
            )
        )
        favouriteViewModel =
            ViewModelProvider(this, favouriteViewModelFactory)[FavouriteViewModel::class.java]

        homeViewModel =
            ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        placesAdapter = PlacesAdapter(this)

        binding.addCityFloatingActionButton.setOnClickListener {


            // TODO: openmap return lat log,


//            homeViewModel.getForecastData(lat, lon)

            lifecycleScope.launch {
                homeViewModel.forecastStateFlow.collectLatest { result ->
                    when (result) {
                        is APIState.Waiting -> {
                            binding.favProgressBar.visibility = View.VISIBLE
                            binding.favouritesRecyclerView.visibility = View.GONE
                            binding.noPlacesImageView.visibility = View.GONE
                            binding.noPlacesTextView.visibility = View.GONE
                            Log.d(TAG, "onCreateView: waiting")

                        }
                        is APIState.Success -> {
                            var favouritePlace = FavouritePlace(
                                result.oneCall.current.dt,
                                result.oneCall.lat,
                                result.oneCall.lon,
                                result.oneCall.timezone
                            )
                            favouriteViewModel.addPlaceToFav(favouritePlace)
                            binding.favProgressBar.visibility = View.GONE
                            binding.favouritesRecyclerView.visibility = View.VISIBLE
                            binding.noPlacesImageView.visibility = View.GONE
                            binding.noPlacesTextView.visibility = View.GONE

                            favouriteViewModel.getAllFavPlaces()

                        }
                        else -> {
                            binding.favProgressBar.visibility = View.GONE
                            Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                "Cant retrieve this place, cant add to fav",
                                Snackbar.LENGTH_LONG
                            ).show()
                            favouriteViewModel.getAllFavPlaces()
                            binding.favProgressBar.visibility = View.GONE
                            binding.favouritesRecyclerView.visibility = View.VISIBLE
                            binding.noPlacesImageView.visibility = View.GONE
                            binding.noPlacesTextView.visibility = View.GONE


                        }
                    }

                }

            }


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
                            "TAG",
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

    }

    override fun displayPlace(favouritePlace: FavouritePlace) {

// TODO:
    }

    override fun deletePlace(favouritePlace: FavouritePlace) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete place from favourite list")
        builder.setMessage("Are you sure to delete this place from favourite?")

        val dialog = builder.create()
        dialog.show()
        builder.setPositiveButton("Yes") { dialog, which ->
            favouriteViewModel.deletePlaceFromFav(favouritePlace)
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

    }
}