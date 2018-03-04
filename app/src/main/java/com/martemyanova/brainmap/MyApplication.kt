package com.martemyanova.brainmap

import android.app.Application

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        BrainMapABTesting.instance.init()
    }
}
