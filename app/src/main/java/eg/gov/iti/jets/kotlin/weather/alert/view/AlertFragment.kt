package eg.gov.iti.jets.kotlin.weather.alert.view

import android.app.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import eg.gov.iti.jets.kotlin.weather.Constants
import eg.gov.iti.jets.kotlin.weather.R

import eg.gov.iti.jets.kotlin.weather.alert.viewmodel.AlertViewModel
import eg.gov.iti.jets.kotlin.weather.alert.viewmodel.AlertViewModelFactory
import eg.gov.iti.jets.kotlin.weather.databinding.AlertDialogBinding
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentAlertBinding
import eg.gov.iti.jets.kotlin.weather.db.LocalSource
import eg.gov.iti.jets.kotlin.weather.model.AlertsDB
import eg.gov.iti.jets.kotlin.weather.model.Repository
import eg.gov.iti.jets.kotlin.weather.network.APIState
import eg.gov.iti.jets.kotlin.weather.network.DayClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AlertFragment : Fragment(), AlertOnClickListener {
    private lateinit var binding: FragmentAlertBinding
    private lateinit var dialogBinding: AlertDialogBinding
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var alarmService: AlarmService
    private lateinit var alertsAdapter: AlertsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        dialogBinding = AlertDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmService = AlarmService(requireContext())
        alertViewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                DayClient.getInstance(), LocalSource(requireContext())
            )
        )
        alertViewModel =
            ViewModelProvider(this, alertViewModelFactory)[AlertViewModel::class.java]

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
                        Log.d(Constants.TAG, "onCreateView: waiting")

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

            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.alert_dialog)
            dialog.show()

            val fromDate = dialog.findViewById<LinearLayout>(R.id.fromDate)
            val toDate = dialog.findViewById<LinearLayout>(R.id.toDate)
            var start = 0L
            var end = 0L
            fromDate.setOnClickListener {
                setAlarm {
                    alarmService.setExactAlarm(it)
                    start = it
                    dialog.findViewById<TextView>(R.id.fromDateTextView).text =
                        getDateFormat("dd-MM", it).toString()
                    dialog.findViewById<TextView>(R.id.fromTimeTextView).text =
                        getDateFormat("hh:mm aa", it).toString()

                }
            }
            toDate.setOnClickListener {
                setAlarm {
                    end = it
                    dialog.findViewById<TextView>(R.id.toDateTextView).text =
                        getDateFormat("dd-MM", it).toString()
                    dialog.findViewById<TextView>(R.id.toTimeTextView).text =
                        getDateFormat("hh:mm aa", it).toString()
                }
            }
            var type = "notification"
            var tag = "any"

            dialog.findViewById<Spinner>(R.id.alertTypeSpinner).onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        tag = parent.getItemAtPosition(position).toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        tag = "any"
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

                alertViewModel.addAlert(
                    AlertsDB(
                        type = type,
                        start = start,
                        end = end,
                        description = "",
                        tag = tag,
                        repeated = false
                    )
                )
                //TODO set alarm to service alarm receiver
//                alarmService.setExactAlarm( )
                alertViewModel.getAllAlerts()
                dialog.dismiss()
            }


//            val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)

//            builder.setPositiveButton(context?.getString(R.string.yes)) { _, _ ->
//                favouriteViewModel.deletePlaceFromFav(favouritePlace)
//            }
//            builder.setNegativeButton(context?.getString(R.string.no)) { dialog, _ ->
//                dialog.dismiss()
//            }
//            val dialog = builder.create()

        }

    }

    private fun setAlarm(callback: (Long) -> Unit): Pair<String, String> {
        var date = ""
        var time = ""
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            DatePickerDialog(
                requireContext(),
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    date = "$year/$month/$day"
                    TimePickerDialog(
                        requireContext(),
                        0,
                        { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            time = "$hour:$minute"
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


            ).show()
        }
        return Pair(date, time)
    }


    private fun getDateFormat(pattern: String, date: Long) =
        SimpleDateFormat(pattern).format(Date(date))

    override fun deleteAlert(alertsDB: AlertsDB) {

        val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
        builder.setTitle(context?.getString(R.string.delete_alert))
        builder.setMessage(context?.getString(R.string.are_you_sure_to_delete))
        builder.setIcon(R.drawable.baseline_delete_24)
        builder.setPositiveButton(context?.getString(R.string.yes)) { _, _ ->
            alertViewModel.deleteAlert(alertsDB)
            //TODO delete from alert receiver
        }

        builder.setNegativeButton(context?.getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()

    }
}