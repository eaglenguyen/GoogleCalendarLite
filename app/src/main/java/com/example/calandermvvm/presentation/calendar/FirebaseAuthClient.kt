package com.example.calandermvvm.presentation.calendar

import android.content.Context
import android.util.Log
import com.example.calandermvvm.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseAuthClient(
    private val context: Context
){

    private val Firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun logOut() {
        auth.signOut()
    }


    fun saveEvent(events: Events, userId: String) {
        Firestore.collection(userId)
            .add(events)
            .addOnSuccessListener {
                Log.d(TAG, "Added")
            }
            .addOnFailureListener{
                Log.w(TAG, "Error")
            }
    }



    fun readEvent(date: String, userId: String, eventList: MutableList<Events>, adapter: EventsAdapter) {
        Firestore.collection(userId)
            .whereEqualTo("date", date)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    Log.w(TAG, "Listener failed", exception)
                }
                if (snapshots != null) {
                    eventList.clear()
                    for (document in snapshots) {
                        val eventss = Events(
                            event = document.data["event"].toString(),
                            timeStamp = document.data["timeStamp"].toString(),
                            date = document.data["date"].toString()
                        )
                        eventList.add(eventss)
                        Log.d(TAG, "Document ID:${document.id} => Event:${document.data["event"]}")
                    }
                    adapter.notifyDataSetChanged()

                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
            }


    /*
        fun readData(taskList: MutableList<Task>, adapter: NotesAdapter) {


            databaseRef = FirebaseDatabase.getInstance().reference
                .child("Tasks")
                .child(auth.currentUser?.uid.toString())

            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    taskList.clear()
                    // key is userID and value is TaskID + Task
                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.key?.let {
                            Task(it,taskSnapshot.value.toString())
                        }
                        if (task != null) {
                            taskList.add(task)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Error", error.toException())
                }

            })
        }

     */

}