package com.example.contactsapp.util

import android.Manifest
import androidx.fragment.app.Fragment
import com.example.contactsapp.R

class CallLogPermissionRequester(fragment: Fragment, onGranted: () -> Unit, onDenied: () -> Unit) : BasePermissionRequester(fragment, onGranted, onDenied) {

    override val permissions: Array<String> = arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG)
    override val titleResId = R.string.call_log_permission
    override val descriptionResId = R.string.android_permission_call_log_description
    override val descriptionWhenDeniedResId = R.string.android_permission_call_log_denied_description
}