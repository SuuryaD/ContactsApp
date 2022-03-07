package com.example.contactsapp.util

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import com.example.contactsapp.R

class PhonePermissionRequester(fragment: Fragment, onGranted: () -> Unit, onDismissed: () -> Unit) : BasePermissionRequester(fragment, onGranted, onDismissed) {

    override val permissions: Array<String> = arrayOf(Manifest.permission.CALL_PHONE)
    override val titleResId = R.string.phone_permission
    override val descriptionResId = R.string.android_permission_phone_description
    override val descriptionWhenDeniedResId = R.string.android_permission_phone_denied_description



}