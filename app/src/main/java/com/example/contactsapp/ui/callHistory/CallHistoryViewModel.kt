package com.example.contactsapp.ui.callHistory

import android.annotation.SuppressLint
import android.content.Context
import android.provider.CallLog
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.*
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.database.CallHistory
import com.example.contactsapp.domain.model.*
import com.example.contactsapp.util.Event
import kotlinx.coroutines.*

class CallHistoryViewModel(
    private val dataSource: ContactsDataSource,
    val context: Context
) : ViewModel() {

    private val _data = MutableLiveData<List<RecyclerViewViewType>>()
    val callHistory: LiveData<List<RecyclerViewViewType>>
        get() = _data

    private val _navigateToCallHistoryDetail = MutableLiveData<Event<CallHistoryData>>()
    val navigateToCallHistoryDataDetail: LiveData<Event<CallHistoryData>>
        get() = _navigateToCallHistoryDetail


    @SuppressLint("Range")
    fun importCallLog() {
        val cres = context.contentResolver

        val cursor = cres?.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )

        val callHistoryList = ArrayList<CallHistoryTemp>()

        while (cursor != null && cursor.moveToNext()) {

            val number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
            val date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))
            val duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))
            val type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
            val id = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID))
            val name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
//            CallLog.Calls.
            callHistoryList.add(
                CallHistoryTemp(
                    id, number, date, duration, type
                )
            )
        }

        CoroutineScope(Dispatchers.Main).launch {
            dataSource.insertCallLog(callHistoryList)
        }

    }

    fun getCallHistory2() {
        CoroutineScope(Dispatchers.IO).launch {
            getCallHistory()
        }
    }

    suspend fun getCallHistory() {

        var ls: List<CallHistory> = emptyList()

        ls = dataSource.getCallLog()

        var ls3 = ArrayList<RecyclerViewViewType>()

        val temp = ArrayList<ArrayList<CallHistory>>()

        CoroutineScope(Dispatchers.IO).launch {

            if (ls.isNotEmpty()) {
                temp.add(arrayListOf(ls[0]))
            }
            for (i in 1 until ls.size) {

                if (ls[i].number != ls[i - 1].number) {
                    temp.add(arrayListOf(ls[i]))
                } else {
                    temp.last().add(ls[i])
                }
            }

            val ls4 = dataSource.getContactNames(temp)
            Log.i("CallHistoryViewModel", temp.toString())

            if (ls4.isNotEmpty() && DateUtils.isToday(ls4[0].callHistoryApi.first().date)) {
                ls3.add(AlphabetHeaderType("Today"))
            }

            var add = true
            for (i in ls4) {

                if (!DateUtils.isToday(i.callHistoryApi.first().date) && add) {
                    ls3.add(AlphabetHeaderType("Older"))
                    add = false
                }
                ls3.add(i)
            }

            Log.i("CallHistoryViewModel", ls3.toString())
            Log.i("CallHistoryViewModel", ls4.toString())

            withContext(Dispatchers.Main) {
                _data.value = ls3
            }
        }
    }

    fun navigateToCallHistory(callHistoryData: CallHistoryData) {
        _navigateToCallHistoryDetail.value = Event(callHistoryData)
    }

}