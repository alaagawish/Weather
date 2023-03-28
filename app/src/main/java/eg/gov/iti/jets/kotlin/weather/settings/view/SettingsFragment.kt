package eg.gov.iti.jets.kotlin.weather.settings.view

import android.Manifest.permission_group.LOCATION
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.kotlin.weather.*
import eg.gov.iti.jets.kotlin.weather.Constants.LANGUAGE
import eg.gov.iti.jets.kotlin.weather.Constants.NOTIFICATION
import eg.gov.iti.jets.kotlin.weather.Constants.UNIT
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentSettingsBinding
import eg.gov.iti.jets.kotlin.weather.db.LocalSource
import eg.gov.iti.jets.kotlin.weather.model.Repository
import eg.gov.iti.jets.kotlin.weather.network.DayClient
import eg.gov.iti.jets.kotlin.weather.settings.viewmodel.SettingsViewModel
import eg.gov.iti.jets.kotlin.weather.settings.viewmodel.SettingsViewModelFactory
import java.util.*

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var settingsViewModelFactory: SettingsViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModelFactory = SettingsViewModelFactory(
            Repository.getInstance(
                DayClient.getInstance(),
                LocalSource(requireContext())
            )
        )
        settingsViewModel = ViewModelProvider(
            this, settingsViewModelFactory
        )[SettingsViewModel::class.java]
        if (sharedPreferences.getString(LANGUAGE, null) == "en") {
            binding.englishRadioButton.isChecked = true
        } else
            binding.arabicRadioButton.isChecked = true

        if (sharedPreferences.getString(LOCATION, null) == "map") {
            binding.mapRadioButton.isChecked = true
        } else
            binding.gpsRadioButton.isChecked = true
        if (sharedPreferences.getString(NOTIFICATION, null) == "enable") {
            binding.enableNotificationsRadioButton.isChecked = true
        } else
            binding.disableNotificationsRadioButton.isChecked = true

        if (sharedPreferences.getString(UNIT, null) == "metric") {
            binding.celsiusRadioButton.isChecked = true
        } else if (sharedPreferences.getString(UNIT, null) == "imperial")
            binding.fahrenheitRadioButton.isChecked = true
        else
            binding.kelvinRadioButton.isChecked = true


        binding.languagesRadioGroup.setOnCheckedChangeListener { _, i ->
            val radioButton = view.findViewById<RadioButton>(i)
            when (radioButton.text) {
                context?.getString(R.string.english) -> editor.putString(LANGUAGE, "en")
                context?.getString(R.string.arabic) -> editor.putString(LANGUAGE, "ar")
            }
            editor.apply()

            val locale = sharedPreferences.getString(LANGUAGE, "en")?.let { Locale(it) }
            if (locale != null) {
                Locale.setDefault(locale)
            }
            val res: Resources = context?.resources!!
            val configuration = Configuration(res.configuration)
            configuration.locale = locale
            res.updateConfiguration(configuration, res.displayMetrics)

            val mainActivity = activity as MainActivity
            mainActivity.navController.navigate(R.id.settingsFragment)

        }
        binding.locationRadioGroup.setOnCheckedChangeListener { _, i ->
            val radioButton = view.findViewById<RadioButton>(i)
            when (radioButton.text) {
                context?.getString(R.string.map) -> editor.putString(LOCATION, "map")
                context?.getString(R.string.gps) -> editor.putString(LOCATION, "gps")
            }
            editor.apply()
        }
        binding.notificationsRadioGroup.setOnCheckedChangeListener { _, i ->
            val radioButton = view.findViewById<RadioButton>(i)
            when (radioButton.text) {
                context?.getString(R.string.enable) -> editor.putString(NOTIFICATION, "enable")
                context?.getString(R.string.disable) -> editor.putString(NOTIFICATION, "disable")
            }
            editor.apply()


        }
        binding.unitsGroup.setOnCheckedChangeListener { _, i ->
            val radioButton = view.findViewById<RadioButton>(i)
            when (radioButton.text) {

                context?.getString(R.string.celsius) -> editor.putString(UNIT, "metric")
                context?.getString(R.string.fahrenheit) -> editor.putString(UNIT, "imperial")
                context?.getString(R.string.kelvin) -> editor.putString(UNIT, "standard")
            }
            editor.apply()

        }

    }

}