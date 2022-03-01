package com.example.contactsapp.domain.model

import java.time.Duration
import java.util.*

data class CallHistoryApi(val number: String, val date: Long, val duration: String, val type: Int)
