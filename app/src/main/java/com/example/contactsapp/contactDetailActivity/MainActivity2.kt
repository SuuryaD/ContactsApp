package com.example.contactsapp.contactDetailActivity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.contactsapp.R
import com.example.contactsapp.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent?.data
         val extras = intent?.extras
        val type = intent?.type

        val id = data?.lastPathSegment
        Log.i("MainActivity2", "data: $data, extras: $extras, type: $type, id:$id"  )

        setSupportActionBar(binding.toolbar)
        val bundle = Bundle()
        bundle.putString("data", data.toString())

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.setGraph(R.navigation.nav_graph2, bundle)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}