package com.example.contactsapp.util

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import android.util.Log

class ZohoSyncAdapter(context: Context) : AbstractThreadedSyncAdapter(context,false) {

    override fun onPerformSync(
        account: Account?,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient?,
        syncResult: SyncResult?
    ) {
        Log.i("ZohoSyncAdapter", "onPerformSync method called")
    }
}