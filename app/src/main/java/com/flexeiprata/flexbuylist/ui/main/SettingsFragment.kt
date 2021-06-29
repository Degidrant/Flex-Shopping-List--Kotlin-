package com.flexeiprata.flexbuylist.ui.main

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.flexeiprata.flexbuylist.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

    }
}