package com.flexeiprata.flexbuylist.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.flexeiprata.flexbuylist.R
import com.flexeiprata.flexbuylist.databinding.FragmentThirdScreenBinding


class ThirdScreenFragment : Fragment() {

    private var _binding: FragmentThirdScreenBinding? = null
    private val binding get() = _binding!!

    companion object{
        fun newInstance(): ThirdScreenFragment{
            return ThirdScreenFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdScreenBinding.inflate(inflater, container, false)


        return binding.root
    }
}