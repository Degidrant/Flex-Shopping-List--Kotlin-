package com.flexeiprata.flexbuylist.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.flexeiprata.flexbuylist.R
import com.flexeiprata.flexbuylist.adapters.FIRST_LAUNCH
import com.flexeiprata.flexbuylist.adapters.SHARED_PREF_TAG
import com.flexeiprata.flexbuylist.databinding.ViewpagerFragmentBinding

class ViewPagerFragment : Fragment() {

    private var _binding: ViewpagerFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_TAG, Context.MODE_PRIVATE)
        if (sharedPref.getBoolean(FIRST_LAUNCH, false)) {
            val action = ViewPagerFragmentDirections.toMainFragment()
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewpagerFragmentBinding.inflate(inflater, container, false)

        val fragmentList = arrayListOf(
            FirstScreenFragment.newInstance(), SecondScreenFragment.newInstance(),
            ThirdScreenFragment.newInstance(), LastScreenFragment.newInstance()
        )

        val adapter = ViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager,
        lifecycle)
        binding.viewPagerOn.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.viewPagerOn.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                binding.button.text = if(position == 3)
                    getString(R.string.finish)
                        else
                    getString(R.string.next)
            }
        })

        binding.button.setOnClickListener {
            if (binding.viewPagerOn.currentItem < 3) binding.viewPagerOn.currentItem++

                else {
                    val sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_TAG, Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putBoolean(FIRST_LAUNCH, true)
                    editor.apply()
                    val action = ViewPagerFragmentDirections.toMainFragment()
                    findNavController().navigate(action)
            }
        }
    }
}