package eg.gov.iti.jets.kotlin.weather.settings.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.alert.viewmodel.AlertViewModel
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentAlertBinding
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentSettingsBinding
import eg.gov.iti.jets.kotlin.weather.settings.viewmodel.SettingsViewModel

class SettingsFragment : Fragment() {


    private var _binding: FragmentSettingsBinding? = null

    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textSettings
//        settingViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}