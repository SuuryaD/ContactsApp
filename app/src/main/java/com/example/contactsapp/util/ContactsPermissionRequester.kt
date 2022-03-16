package com.example.contactsapp.util

import android.Manifest
import androidx.fragment.app.Fragment
import com.example.contactsapp.R

class ContactsPermissionRequester(fragment: Fragment, onGranted: () -> Unit, onDenied: () -> Unit) :
    BasePermissionRequester(fragment, onGranted, onDenied) {

    override val permissions: Array<String> =
        arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
    override val titleResId = R.string.contact_permission
    override val descriptionResId = R.string.android_permission_contact_description
    override val descriptionWhenDeniedResId = R.string.android_permission_contact_denied_description

}