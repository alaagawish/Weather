package eg.gov.iti.jets.kotlin.weather.alert.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.kotlin.weather.alert.viewmodel.AlertViewModel
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentAlertBinding


class AlertFragment : Fragment() {

    private var _binding: FragmentAlertBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val alertViewModel =
            ViewModelProvider(this).get(AlertViewModel::class.java)

        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        alertViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}