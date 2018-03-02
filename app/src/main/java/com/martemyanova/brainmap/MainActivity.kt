package com.martemyanova.brainmap

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val brainMapViewModel by lazy {
        ViewModelProviders.of(this).get(BrainMapViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        radarChart.setData(brainMapViewModel.categories,
                brainMapViewModel.yourScore, brainMapViewModel.ageGroupScore)
    }
}
