package com.example.contactsapp

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val accountManager = AccountManager.get(applicationContext)

//        Account("surya@zoho.com", "com.surya").also {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val added =  accountManager.addAccountExplicitly(it, "12345", null)
//                Log.i("MainActivity", added.toString())
//            }
//        }

        val navController = this.findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.bottomNavView)
        navView.setupWithNavController(navController)

        val appBar = AppBarConfiguration
            .Builder(
                R.id.contactsFragment,
                R.id.callHistoryFragment,
                R.id.favoritesFragment,
                R.id.dialFragment
            )
            .build()


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.contactsFragment || destination.id == R.id.callHistoryFragment || destination.id == R.id.favoritesFragment
                || destination.id == R.id.dialFragment
            ) {
                navView.visibility = View.VISIBLE
            } else
                navView.visibility = View.INVISIBLE
        }
        NavigationUI.setupActionBarWithNavController(this, navController, appBar)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

//TODO 2 way sync contacts

//TODO call log paging