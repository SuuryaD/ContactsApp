package com.example.contactsapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ContactDetails::class, ContactPhoneNumber::class, CallHistory::class],
    version = 14,
    exportSchema = false
)
abstract class ContactDatabase : RoomDatabase() {

    abstract val contactDetailsDao: ContactDetailsDao

    companion object {

        @Volatile
        private var INSTANCE: ContactDatabase? = null

//        fun getInstance(context: Context): ContactDatabase {
//            synchronized(this) {
//                var instance = INSTANCE
//
//                if (instance == null) {
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        ContactDatabase::class.java,
//                        "contact_database"
//                    )
//                        .fallbackToDestructiveMigration()
//                        .build()
//                    INSTANCE = instance
//                }
//                return instance
//            }
//        }
    }

}