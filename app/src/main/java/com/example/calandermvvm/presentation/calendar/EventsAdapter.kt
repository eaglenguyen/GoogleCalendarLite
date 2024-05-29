package com.example.calandermvvm.presentation.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calandermvvm.OnClick
import com.example.calandermvvm.databinding.EventItemRvBinding


class EventsAdapter(private val list: MutableList<Events>,private val listener: OnClick): RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {



        inner class EventViewHolder(val binding: EventItemRvBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                deleteEv.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val event = list[position]
                        listener.deleteEvent(event)
                    }

                }
                editEv.setOnClickListener{
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = list[position]
                        listener.editEvent(task)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = EventItemRvBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = list[position]
        holder.binding.apply {
            eventItem.text = event.event
            timeStamp.text = event.timeStamp
        }
    }


    }