package com.example.clientchatapp.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.clientchatapp.R
import com.example.clientchatapp.databinding.FragmentHomeBinding
import com.example.clientchatapp.viewmodel.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val user = arguments?.let { HomeFragmentArgs.fromBundle(it).user }
        if (user != null) {
            sharedViewModel.setUser(user)
        }

        bottomNavView = view.findViewById(R.id.bottom_nav)
        val navController = childFragmentManager.findFragmentById(R.id.nav_host_fragment_home)
            ?.findNavController()

        navController?.let { nav ->
            bottomNavView.setupWithNavController(nav)

            nav.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.loginFragment -> bottomNavView.visibility = View.GONE
                    else -> bottomNavView.visibility = View.VISIBLE
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navController?.popBackStack()
        }
    }
}