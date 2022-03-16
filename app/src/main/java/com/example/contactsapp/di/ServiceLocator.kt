package com.example.contactsapp.di

import android.content.Context
import androidx.room.Room
import com.example.contactsapp.data.database.ContactDatabase
import com.example.contactsapp.data.database.ContactDetailsDao
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.ContactsLocalDataSource

object ServiceLocator {

    private var contactsDatabase: ContactDatabase? = null

    private var contactsDataSource: ContactsDataSource? = null

    fun provideContactsDataSource(context: Context): ContactsDataSource {
        return contactsDataSource ?: synchronized(this) {
            return contactsDataSource ?: createContactsLocalDataSource(context)
        }
    }

    private fun createContactsLocalDataSource(context: Context): ContactsDataSource {
        val newSrc = ContactsLocalDataSource(createContactsDatabase(context))
        contactsDataSource = newSrc
        return newSrc
    }

    private fun createContactsDatabase(context: Context): ContactDetailsDao {
        val contactsDatabase = contactsDatabase ?: createDatabase(context)
        return contactsDatabase.contactDetailsDao
    }

    private fun createDatabase(context: Context): ContactDatabase {

        val res = Room.databaseBuilder(
            context.applicationContext,
            ContactDatabase::class.java,
            "contact_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        contactsDatabase = res
        return res
    }

}