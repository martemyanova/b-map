package com.martemyanova.brainmap

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val brainMapViewModel by lazy {
        ViewModelProviders.of(this).get(BrainMapViewModel::class.java)
    }

    private val lightTypeface by lazy {
        Typeface.createFromAsset(assets,
                String.format(Locale.US,"fonts/%s", "GothamSSm-Light.ttf"))
    }

    private val mediumTypeface by lazy {
        Typeface.createFromAsset(assets,
                String.format(Locale.US,"fonts/%s", "GothamSSm-Medium.ttf"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        titleText.typeface = Typeface.create(mediumTypeface, Typeface.BOLD)
        descriptionText.typeface = lightTypeface

        btn1.typeface = lightTypeface
        btn2.typeface = lightTypeface
        btn3.typeface = lightTypeface

        radarChart.setData(brainMapViewModel.categories,
                brainMapViewModel.yourScore, brainMapViewModel.ageGroupScore)
    }
}
