package com.jasjus.pmob4

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import android.os.Handler
import android.os.Looper

class AppExecutor {
    //kelas ini berfungsi untuk menjalankan database lokal diluar main thread
    //tanpa kelas ini maka proses CRUD tidak dapat dilakukan/errror
    val diskIO: Executor = Executors.newSingleThreadExecutor()
    val networkIO: Executor = Executors.newFixedThreadPool(3)
    val mainThread: Executor = MainThreadExecutor()
    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}