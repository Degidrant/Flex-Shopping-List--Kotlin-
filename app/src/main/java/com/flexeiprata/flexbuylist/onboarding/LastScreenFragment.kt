package com.flexeiprata.flexbuylist.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.flexeiprata.flexbuylist.adapters.FIRST_LAUNCH
import com.flexeiprata.flexbuylist.adapters.SHARED_PREF_TAG
import com.flexeiprata.flexbuylist.databinding.FragmentLastScreenBinding


class LastScreenFragment : Fragment() {

    private var _binding: FragmentLastScreenBinding? = null
    private val binding get() = _binding!!

    companion object{
        fun newInstance(): LastScreenFragment{
            return LastScreenFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLastScreenBinding.inflate(inflater, container, false)


        return binding.root
    }
}