package com.example.contactsapp.ui.searchFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.R



/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_search, container, false)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.contact_list_menu, menu)

        val searchView: SearchView = menu.findItem(R.id.actionSearch).actionView as SearchView

//        searchView.isIconifiedByDefault = false

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                Log.i("SearchFragment", "close called")
                this@SearchFragment.findNavController().navigateUp()
                return false
            }

        })

//        searchView.isIconified = false

//        searchView.setOnCloseListener {
//            Log.i("SearchFragment", "close called")
//            this@SearchFragment.findNavController().navigateUp()
//            true
//        }

//        searchView.
        searchView.requestFocus()



        super.onCreateOptionsMenu(menu, inflater)

    }
}