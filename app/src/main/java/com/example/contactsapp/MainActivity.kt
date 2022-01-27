package com.example.contactsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = this.findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.bottomNavView)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.contactsFragment || destination.id == R.id.blankFragment){
                navView.visibility = View.VISIBLE
            }
            else
                navView.visibility = View.INVISIBLE
        }

//
//        val temp = ContactDetail("surya", "surya@email", listOf("123456789", "987654321"))
//        val temp2 = ContactDetail("dhanush", "dhanush@email", listOf("1111111111", "2222222222"))
//
//        val application = requireNotNull(this).application
//        val db = ContactDatabase.getInstance(application).contactDetailsDao
//
//        val res = db.getAll()
//        res.observe(this, Observer {
//            Log.i("MainActivity", it.toString())
//        })
//        CoroutineScope(Dispatchers.IO).launch {
//            db.insert(temp)
////            Log.i("MainActivity", db.getAll().toString())
////            db.deleteContact(1)
////            Log.i("MainActivity", db.getAll().toString())
////            db.deleteContact(2)
////            Log.i("MainActivity", db.getAll().toString())
////            db.deleteContact(3)
////            Log.i("MainActivity", db.getAll().toString())
////            Log.i("MainActivity", db.getphoneNumber(3).toString())
////            Log.i("MainActivity", db.getphoneNumber(5).toString())
//
//        }
        
    }
}