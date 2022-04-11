package com.example.contactsapp.util

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ZohoAccountService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        val accountAuthenticator = ZohoAccountAuthenticator(this.applicationContext)
        return accountAuthenticator.iBinder
    }
}