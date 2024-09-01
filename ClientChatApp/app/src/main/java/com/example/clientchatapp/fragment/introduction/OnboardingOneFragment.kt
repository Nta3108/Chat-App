package com.example.clientchatapp.fragment.introduction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.clientchatapp.R
import com.example.clientchatapp.databinding.FragmentOnboardingOneBinding
import com.example.clientchatapp.databinding.FragmentOnboardingTwoBinding


class OnboardingOneFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingOneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingOneFragment_to_onboardingTwoFragment)
        }
    }
}