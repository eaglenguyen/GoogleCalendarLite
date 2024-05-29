package com.example.calandermvvm

import com.example.calandermvvm.presentation.calendar.Events

interface OnClick {
    fun deleteEvent(events: Events)
    fun editEvent(events: Events)
}