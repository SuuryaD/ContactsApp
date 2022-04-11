package com.example.contactsapp.util

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ZohoSyncService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        val syncAdapter = ZohoSyncAdapter(this.baseContext)
        return syncAdapter.syncAdapterBinder
    }
}