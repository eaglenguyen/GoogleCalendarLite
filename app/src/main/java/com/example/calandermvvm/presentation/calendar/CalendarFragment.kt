package com.example.calandermvvm.presentation.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.calandermvvm.R
import com.example.calandermvvm.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val intent: CalendarFragmentArgs by navArgs()

    private val fireBaseClient by lazy {
        FirebaseAuthClient(
            context = requireActivity().applicationContext
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = view.findViewById<CalendarView>(R.id.calView)

        val emailUser = view.findViewById<TextView>(R.id.userEmail)
        emailUser.text = intent.email
        val userId = intent.user

        calendar.setOnDateChangeListener { _, year, month, day ->
            // month + 1 because index starts at 0 = jan
            // val msg = "Selected date is " + (month + 1) + "/" +  day + "/" + year
            val action = CalendarFragmentDirections.actionCalendarFragmentToEventFragment(day,month,year, userId)
            findNavController().navigate(action)
            // Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

        }
        binding.logOut.setOnClickListener {
            fireBaseClient.logOut()
            val action = CalendarFragmentDirections.actionCalendarFragmentToLoginFragment()
            findNavController().navigate(action)
            Toast.makeText(requireActivity().applicationContext, "Logged out!", Toast.LENGTH_SHORT).show()
        }



    }


}