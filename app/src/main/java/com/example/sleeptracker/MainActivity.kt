package com.example.sleeptracker

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
//    lateinit var job1: Job
//    lateinit var job2: Job
//    lateinit var job3: Job
//    lateinit var job4: Job
//    lateinit var job5: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Log.i("tesssssssssst ", "tst")
//        val expectationHnadler = CoroutineExceptionHandler { coroutineContext, throwable ->
//            Log.i("modesto", throwable.message.toString() + "modesto")
//
//        }
//        job1 = lifecycleScope.launch(expectationHnadler) {
//            delay(2000)
//            Log.i("job 1 ", "job 1")
//            job2 = launch {
//                delay(2000)
//                Log.i("job 2 ", "job 2")
//
//                job4 = launch {
//                    delay(2000)
//                    Log.i("job 4 ", "job 4")
//
//                }
//                job5 = launch {
//                    delay(2000)
//                    Log.i("job 5 ", "job 5 ")
//
//                }
//            }
//            job3 = launch (){
//                delay(2000)
//                //val result = 5 / 0
//                Log.i("job 3 ", "job 3")
//
//            }
//        }
    }
}