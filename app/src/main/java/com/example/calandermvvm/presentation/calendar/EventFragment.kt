package com.example.calandermvvm.presentation.calendar

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calandermvvm.EventNotificationReceiver
import com.example.calandermvvm.OnClick
import com.example.calandermvvm.R
import com.example.calandermvvm.TAG
import com.example.calandermvvm.databinding.FragmentEventBinding
import com.example.calandermvvm.notificationID
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint

class EventFragment : Fragment(), OnClick {

    private lateinit var adapter: EventsAdapter
    private lateinit var eventList: MutableList<Events>
    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    private val intent: EventFragmentArgs by navArgs()
    private lateinit var fbDatabase: FirebaseFirestore

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
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        eventList = mutableListOf()
        adapter = EventsAdapter(eventList, this)
        binding.recyclerView.adapter = adapter








        binding.apply {
            val userId = intent.userId
            val day = intent.day
            // month # starts at 0
            val month = intent.month
            val year = intent.year

            val date = String.format("%s/%s/%s", month + 1, day, year)

            currentDate.text = date


            addButton.setOnClickListener{
                val hour = binding.timePicker.hour
                val min = binding.timePicker.minute
                val amPm = if (hour < 12) "AM" else "PM"
                val textField = eventsField.text.toString()
                val timeStamp = String.format("%s:%02d %s", hour, min, amPm)
                val eventData = Events(textField,timeStamp,date)

                val calendar =  Calendar.getInstance()
                calendar.set(year,month,day,hour,min)


                if (textField.isEmpty()) {
                    eventsField.error = "Empty!"
                    return@setOnClickListener
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    fireBaseClient.saveEvent(eventData, userId)
                }

                showNotification(calendar.timeInMillis, textField)
                showPopUp(calendar.timeInMillis, textField)

                eventsField.text?.clear()
            }



            backButton.setOnClickListener{
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                    fireBaseClient.readEvent(date,userId,eventList,adapter)
            }

        }



    }




    private fun showNotification(time: Long, title: String) {
        val activityIntent = Intent(requireContext().applicationContext, EventNotificationReceiver::class.java).apply {
            putExtra("eventTitle", title)

        }
        val eventIntent = PendingIntent.getBroadcast(
            requireContext().applicationContext,
            notificationID,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            eventIntent
        )
    }

    // Dialog pop up after saving event
    private fun showPopUp(time: Long, title: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireContext().applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext().applicationContext)
        AlertDialog.Builder(requireContext())
            .setTitle("Event Saved!")
            .setMessage( title +
                    "\n" + dateFormat.format(date) + " " +
                    "\n" + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }


    override fun deleteEvent(events: Events) {
        fbDatabase = FirebaseFirestore.getInstance()
        val userId = intent.userId


        fbDatabase.collection(userId)
            .whereEqualTo("event", events.event)
            .get()
            .addOnSuccessListener { results ->
                for (document in results) {
                    document.reference.delete().addOnSuccessListener {
                        Log.d(TAG, " Successfully Deleted!")
                    }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error getting Docs", e)
                        }
                }
            }
    }



    override fun editEvent(events: Events) {
        fbDatabase = FirebaseFirestore.getInstance()
        val userId = intent.userId

        val mDialog = AlertDialog.Builder(this.requireContext())
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_dialog_box, null)

        val eventName = mDialogView.findViewById<EditText>(R.id.eventUpdate)
        val updateBtn = mDialogView.findViewById<Button>(R.id.btnUpdate)

        // Sets editText to clicked event
        eventName.setText(events.event)


        mDialog.setView(mDialogView)
        val alertDialog = mDialog.create()
        alertDialog.show()

        updateBtn.setOnClickListener {
            val newEvent = eventName.text.toString()
            viewLifecycleOwner.lifecycleScope.launch {
                fbDatabase.collection(userId)
                    .whereEqualTo("event", events.event)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                document.reference.update("event", newEvent).addOnSuccessListener {
                                    Log.d(TAG, " Successfully Updated!")
                                } .addOnFailureListener { e ->
                                    Log.w(TAG, "Error getting Docs", e)
                                }
                            }
                        }
                    }

            }
            alertDialog.dismiss()
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}