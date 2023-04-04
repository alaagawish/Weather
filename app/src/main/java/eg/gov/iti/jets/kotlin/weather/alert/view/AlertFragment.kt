package eg.gov.iti.jets.kotlin.weather.alert.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LATITUDE
import eg.gov.iti.jets.kotlin.weather.R

import eg.gov.iti.jets.kotlin.weather.alert.viewmodel.AlertViewModel
import eg.gov.iti.jets.kotlin.weather.alert.viewmodel.AlertViewModelFactory
import eg.gov.iti.jets.kotlin.weather.databinding.AlertDialogBinding
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentAlertBinding
import eg.gov.iti.jets.kotlin.weather.db.DayDatabase
import eg.gov.iti.jets.kotlin.weather.db.LocalSource
import eg.gov.iti.jets.kotlin.weather.viewmodel.HomeViewModel
import eg.gov.iti.jets.kotlin.weather.viewmodel.HomeViewModelFactory
import eg.gov.iti.jets.kotlin.weather.model.Alert
import eg.gov.iti.jets.kotlin.weather.model.AlertsDB
import eg.gov.iti.jets.kotlin.weather.model.Repository
import eg.gov.iti.jets.kotlin.weather.model.APIState
import eg.gov.iti.jets.kotlin.weather.network.DayClient
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import eg.gov.iti.jets.kotlin.weather.utils.Constants.NOTIFICATION
import eg.gov.iti.jets.kotlin.weather.utils.ConvertTime
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*


class AlertFragment : Fragment(), AlertOnClickListener {
    private lateinit var binding: FragmentAlertBinding

    private lateinit var dialogBinding: AlertDialogBinding
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var alarmService: AlarmService
    private lateinit var alertsAdapter: AlertsAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        dialogBinding = AlertDialogBinding.inflate(inflater, container, false)
        dialogBinding.root
        return binding.root
    }

    @SuppressLint("LogNotTimber")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertViewModelFactory = AlertViewModelFactory(
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
        alertViewModel =
            ViewModelProvider(this, alertViewModelFactory)[AlertViewModel::class.java]

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
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]


        alarmService = AlarmService(requireContext())
        alertsAdapter = AlertsAdapter(this)

        lifecycleScope.launch {
            alertViewModel.alertsLocalPlacesStateFlow.collectLatest { result ->
                when (result) {
                    is APIState.Waiting -> {
                        binding.noAlertImageView.visibility = View.GONE
                        binding.noAlertTextView.visibility = View.GONE
                        binding.alertsRecyclerView.visibility = View.GONE
                        binding.alertProgressBar.visibility = View.VISIBLE
                        Timber.tag(Constants.TAG).d("onCreateView: waiting")

                    }
                    is APIState.SuccessRoomAlerts -> {
                        if (result.list.isNotEmpty()) {
                            alertsAdapter.submitList(result.list)
                            binding.alertsRecyclerView.adapter = alertsAdapter
                            binding.noAlertImageView.visibility = View.GONE
                            binding.noAlertTextView.visibility = View.GONE
                            binding.alertsRecyclerView.visibility = View.VISIBLE
                            binding.alertProgressBar.visibility = View.GONE
                        } else {
                            binding.noAlertImageView.visibility = View.VISIBLE
                            binding.noAlertTextView.visibility = View.VISIBLE
                            binding.alertsRecyclerView.visibility = View.GONE
                            binding.alertProgressBar.visibility = View.GONE
                        }
                    }
                    else -> {
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "Error in retrieving alerts",
                            Snackbar.LENGTH_INDEFINITE
                        ).show()

                        binding.noAlertImageView.visibility = View.GONE
                        binding.noAlertTextView.visibility = View.GONE
                        binding.alertsRecyclerView.visibility = View.GONE
                        binding.alertProgressBar.visibility = View.GONE

                    }
                }

            }
        }

        binding.addAlertFloatingActionButton.setOnClickListener {
            homeViewModel.getForecastData(
                sharedPreferences!!.getString(LATITUDE, "1.0")?.toDouble()!!,
                sharedPreferences!!.getString(Constants.LONGITUDE, "1.0")?.toDouble()!!
            )
            var alerts: List<Alert> = mutableListOf()
            lifecycleScope.launch {
                homeViewModel.forecastStateFlow.collectLatest { result ->
                    when (result) {
                        is APIState.Waiting -> {
                            Timber.d("AlertFragment: waiting")
                        }
                        is APIState.Success -> {
                            if (!result.oneCall.alerts.isNullOrEmpty()) {
                                println("alerts found ${result.oneCall.alerts[0]}")
                                alerts = result.oneCall.alerts
                            }
                            Log.d(
                                Constants.TAG,
                                "AlertFragment: done ${result.oneCall.current.weather[0]}"
                            )

                        }
                        else -> {
                            Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                "Something went wrong, check again later",
                                Snackbar.LENGTH_LONG
                            ).show()

                        }
                    }

                }
            }
            var start = Calendar.getInstance().timeInMillis
            var end = start + 86400000L
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.alert_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.findViewById<TextView>(R.id.toDateTextView).text =
                ConvertTime.getDateFormat("dd-MM", end)
            dialog.findViewById<TextView>(R.id.toTimeTextView).text =
                ConvertTime.getDateFormat("hh:mm aa", end)
            dialog.findViewById<TextView>(R.id.fromDateTextView).text =
                ConvertTime.getDateFormat("dd-MM", start)
            dialog.findViewById<TextView>(R.id.fromTimeTextView).text =
                ConvertTime.getDateFormat("hh:mm aa", start)

            dialog.show()

            val fromDate = dialog.findViewById<LinearLayout>(R.id.fromDate)
            val toDate = dialog.findViewById<LinearLayout>(R.id.toDate)

            fromDate.setOnClickListener {
                setDateAndTime {
                    start = it
                    dialog.findViewById<TextView>(R.id.fromDateTextView).text =
                        ConvertTime.getDateFormat("dd-MM", it)
                    dialog.findViewById<TextView>(R.id.fromTimeTextView).text =
                        ConvertTime.getDateFormat("hh:mm aa", it)

                }
            }
            toDate.setOnClickListener {
                setDateAndTime {
                    end = it
                    println("date ${Calendar.getInstance().timeInMillis}")
                    if (it <= start) {
                        dialog.dismiss()
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "Check date and time of alert",
                            Snackbar.LENGTH_LONG
                        ).show()

                    } else {
                        dialog.findViewById<TextView>(R.id.toDateTextView).text =
                            ConvertTime.getDateFormat("dd-MM", it)
                        dialog.findViewById<TextView>(R.id.toTimeTextView).text =
                            ConvertTime.getDateFormat("hh:mm aa", it)
                    }

                }
            }

            var type = "notification"
            var tag = "Any types"
            var description = "Weather is fine, no alerts found about $tag"
//            var repeated = false

            dialog.findViewById<Spinner>(R.id.alertTypeSpinner).onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        tag = parent.getItemAtPosition(position).toString()
                        println("tagggggggggggg kkkk $tag")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        tag = "Any types"
                    }
                }
            dialog.findViewById<RadioGroup>(R.id.alertMethodRadioGroup)
                .setOnCheckedChangeListener { _, checkedId ->
                    val radioButton = dialog.findViewById<RadioButton>(checkedId)
                    when (radioButton.text) {
                        getString(R.string.notification) -> type = "notification"
                        getString(R.string.alarm) -> type = "alarm"
                    }

                }
            dialog.findViewById<Button>(R.id.saveAlertMaterialButton).setOnClickListener {
                if (sharedPreferences!!.getString(NOTIFICATION, "disable") == "disable") {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Enable notification from settings to receive Notification.",
                        Snackbar.LENGTH_LONG
                    ).show()

                }
                if (alerts.isNotEmpty()) {
                    for (alert in alerts) {
                        if (alert.start * 1000 <= start) {

                            if (tag == alert.event) {
                                description = alert.description
                            }
                            if (tag == "Any types") {
                                tag = alert.event
                                description = alert.description

                            }

                        }
                    }

                }
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    println("notification permission is prohibited,enable it")
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        Constants.PERMISSION_REQUEST_CODE
                    )
                }


                val alert = AlertsDB(
                    id=start,
                    type = type,
                    start = start,
                    end = end,
                    description = description,
                    tag = tag,
                    repeated = false
                )
                alertViewModel.addAlert(alert)
                println(alert.id)
                alarmService.setExactAlarm(start, type, description, "Alert about $tag", alert.id)

                alertViewModel.getAllAlerts()
                dialog.dismiss()
            }

        }

    }


    private fun setDateAndTime(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    TimePickerDialog(
                        requireContext(),
                        0,
                        { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            callback(this.timeInMillis)
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()
                },

                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)

            )
            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }
    }


    override fun deleteAlert(alertsDB: AlertsDB) {
        if (!alertsDB.repeated && alertsDB.start < Calendar.getInstance().timeInMillis) {
            alertViewModel.deleteAlert(alertsDB)
        } else {
            val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
            builder.setTitle(context?.getString(R.string.delete_alert))
            builder.setMessage(context?.getString(R.string.are_you_sure_to_delete))
            builder.setIcon(R.drawable.baseline_delete_24)
            builder.setPositiveButton(context?.getString(R.string.yes)) { _, _ ->
                alarmService.stopAlarm(
                    alertsDB.start,
                    alertsDB.type,
                    alertsDB.description,
                    "Alert about ${alertsDB.tag}",
                    alertsDB.id
                )
                println("sssssssss sss alert id ${alertsDB.id}")
                alertViewModel.deleteAlert(alertsDB)

            }

            builder.setNegativeButton(context?.getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}