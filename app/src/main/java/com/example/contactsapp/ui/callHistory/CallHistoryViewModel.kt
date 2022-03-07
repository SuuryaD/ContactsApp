package com.example.contactsapp.ui.callHistory

import android.telecom.Call
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.domain.model.AlphabetHeaderType
import com.example.contactsapp.domain.model.CallHistory
import com.example.contactsapp.domain.model.CallHistoryApi
import com.example.contactsapp.domain.model.RecyclerViewViewType
import com.example.contactsapp.util.Event
import kotlinx.coroutines.*

class CallHistoryViewModel(private val dataSource: ContactsDataSource) : ViewModel() {


    private val _data = MutableLiveData<List<RecyclerViewViewType>>()
    val callHistory: LiveData<List<RecyclerViewViewType>>
        get() = _data


    private val _navigateToCallHistoryDetail = MutableLiveData<Event<CallHistory>>()
    val navigateToCallHistoryDetail: LiveData<Event<CallHistory>>
        get() = _navigateToCallHistoryDetail

    fun getCallHistory(ls: List<CallHistoryApi>){

        var ls3 = ArrayList<RecyclerViewViewType>()

        val temp = ArrayList<ArrayList<CallHistoryApi>>()

        CoroutineScope(Dispatchers.Main).launch{

            if(ls.isNotEmpty()){
                temp.add(arrayListOf(ls[0]))
            }

            for(i in 1 until ls.size){

                if(ls[i].number != ls[i - 1].number){
                    temp.add(arrayListOf(ls[i]))
                }
                else{
                    temp.last().add(ls[i])
                }
            }

            val ls4 = dataSource.getContactNames(temp)
            Log.i("CallHistoryViewModel", temp.toString())

            if(ls4.isNotEmpty() && DateUtils.isToday(ls4[0].callHistoryApi.first().date)){
                ls3.add(AlphabetHeaderType("Today"))
            }

            var add = true
            for(i in ls4){

                if(!DateUtils.isToday(i.callHistoryApi.first().date) && add){

                    ls3.add(AlphabetHeaderType("Older"))
                    add = false

                }
                ls3.add(i)
            }

            Log.i("CallHistoryViewModel", ls3.toString())
            Log.i("CallHistoryViewModel", ls4.toString())
            _data.value = ls3
        }

    }

    fun navigateToCallHistory(callHistory: CallHistory){
        _navigateToCallHistoryDetail.value = Event(callHistory)
    }


}