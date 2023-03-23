package eg.gov.iti.jets.kotlin.weather.favourite.view

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.kotlin.weather.LANGUAGE
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentFavouriteBinding
import eg.gov.iti.jets.kotlin.weather.favourite.viewmodel.FavouriteViewModel
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import java.util.*

class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val favouriteViewModel =
            ViewModelProvider(this)[FavouriteViewModel::class.java]

        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        val locale = Locale("en")
//        Locale.setDefault(locale)
//        val res: Resources = context?.resources!!
//        val configuration = Configuration(res.configuration)
//        configuration.locale = locale
//        res.updateConfiguration(configuration, res.displayMetrics)
//        val textView: TextView = binding.textGallery
//        favouriteViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}