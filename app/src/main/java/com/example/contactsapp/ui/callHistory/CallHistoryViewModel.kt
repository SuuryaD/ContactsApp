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
import kotlinx.coroutines.*

class CallHistoryViewModel(private val dataSource: ContactsDataSource) : ViewModel() {


    private val _data = MutableLiveData<List<RecyclerViewViewType>>()
    val callHistory: LiveData<List<RecyclerViewViewType>>
        get() = _data


    fun getCallHistory(ls: List<CallHistoryApi>){

        var ls3 = ArrayList<RecyclerViewViewType>()
        var ls2 = ArrayList<CallHistory>()

        val temp = ArrayList<ArrayList<CallHistoryApi>>()

        CoroutineScope(Dispatchers.Main).launch{

            ls2.addAll(dataSource.getContactNames(ls))

            val ls4 = ArrayList<CallHistory>()

            if(ls2.isNotEmpty()){
                ls4.add(ls2[0])
//                temp.add(arrayListOf(ls2[0].callHistoryApi))
            }

            for(i in 1 until ls2.size){

                if(ls2.get(i).callHistoryApi.number != ls2.get(i - 1).callHistoryApi.number){
                    ls4.add(ls2[i])
//                    temp.add(arrayListOf(ls2[i].callHistoryApi))
                }
//                else{
//                    temp.last().add(ls2[i].callHistoryApi)
//                }
            }

            if(ls.isNotEmpty()){
                temp.add(arrayListOf(ls[0]))
            }

            for(i in 1 until ls.size){

                if(ls[i].number != ls[i].number){
                    temp.add(arrayListOf(ls[i]))
                }
                else{
                    temp.last().add(ls[i])
                }
            }

            Log.i("CallHistoryViewModel", temp.toString())

            if(ls4.isNotEmpty() && DateUtils.isToday(ls4[0].callHistoryApi.date)){
                ls3.add(AlphabetHeaderType("Today"))
            }

            var add = true
            for(i in ls4){

                if(!DateUtils.isToday(i.callHistoryApi.date) && add){

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

}