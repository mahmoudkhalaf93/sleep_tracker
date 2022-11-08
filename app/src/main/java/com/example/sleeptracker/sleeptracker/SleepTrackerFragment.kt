/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.sleeptracker.R
import com.example.sleeptracker.databinding.FragmentSleepTrackerBinding
import com.example.sleeptracker.sleeptracker.SleepNightAdapter
import com.example.sleeptracker.sleeptracker.SleepNightListener
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */

class SleepTrackerFragment : Fragment() {

    private lateinit var viewModel: SleepTrackerViewModel


    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_tracker, container, false
        )
        //val application = Application()

        val application = requireNotNull(this.activity).application
        val factory = SleepTrackerViewModelFactory(
            SleepDatabase.getInstance(application).sleepDatabaseDao,
            application
        )

        viewModel = ViewModelProvider(this, factory).get(SleepTrackerViewModel::class.java)
        binding.sleepTrackerViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.showSnackbarEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT
                ).show()


                viewModel.showSnackbarEventDone()
            }
        })
        viewModel.navigateToSleepQuality.observe(viewLifecycleOwner, Observer { night ->
            night?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId)
                )
                viewModel.doneNavigating()
            }
        })
        viewModel.navigateToSleepDataQuality.observe(viewLifecycleOwner, Observer { id ->
            id?.let {
                this.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepDetailFragment(id))
                viewModel.navigateToSleepDataQualityDone()
            }
        })
        val gridLayoutManager = GridLayoutManager(requireActivity(),3)
        binding.sleepList.layoutManager = gridLayoutManager
        //onclick callback
        val adapter = SleepNightAdapter(SleepNightListener { nightId ->
           viewModel.onSleepNightClicked(nightId)
        })


        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (position) {
                0 -> 3
                else -> 1
            }
        }
        binding.sleepList.adapter = adapter

        viewModel.nights.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })

        return binding.root
    }
}

