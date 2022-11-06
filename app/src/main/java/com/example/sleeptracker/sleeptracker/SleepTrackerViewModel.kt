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

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.sleeptracker.formatNights
import kotlinx.coroutines.launch

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    // val viewModelJob = Job()

//    override fun onCleared() {
//        super.onCleared()
//        viewModelJob.cancel()
//    }

    // private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var tonight = MutableLiveData<SleepNight?>()

    val startButtonVisable = Transformations.map(tonight) { tonight ->
        null == tonight
    }
    val stopButtonVisable = Transformations.map(tonight) { tonight ->
        null != tonight
    }


     val nights = database.getAllNights()
    val clearButtonVisable = Transformations.map(nights) {
        it.isNotEmpty()
    }
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    private val _navigateToSleepQuality = MutableLiveData<SleepNight?>()
    val navigateToSleepQuality: LiveData<SleepNight?> get() = _navigateToSleepQuality

    private val _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackbarEvent: LiveData<Boolean> get() = _showSnackbarEvent
    fun showSnackbarEventDone() {
        _showSnackbarEvent.value = false
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {

        viewModelScope.launch { tonight.value = getTonightFromDatabase() }

    }

    fun doneNavigating() {
        _navigateToSleepQuality.value = null;
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {

        var night = database.getTonight()
        if (night?.endTimeMilli != night?.startTimeMilli) {
            night = null
        }
        return night
    }

    fun onStartTracking() {
        viewModelScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun insert(newNight: SleepNight) {
        database.insert(newNight)
    }

    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            _navigateToSleepQuality.value = oldNight
            update(oldNight)
        }
    }

    private suspend fun update(oldNight: SleepNight) {
        database.update(oldNight)
    }

    fun onClear() {
        viewModelScope.launch {
            clear()
            tonight.value = null
            _showSnackbarEvent.value=true
        }
    }

    private suspend fun clear() {
        database.clear()
    }

    private  val _navigateToSleepDataQuality =MutableLiveData<Long?>()
    val navigateToSleepDataQuality : LiveData<Long?> get() = _navigateToSleepDataQuality
    fun onSleepNightClicked(nightId: Long) {
        _navigateToSleepDataQuality.value=nightId
    }
    fun navigateToSleepDataQualityDone(){
        _navigateToSleepDataQuality.value=null
    }


}

