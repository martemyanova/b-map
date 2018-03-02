package com.martemyanova.brainmap

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.martemyanova.brainmap.BrainMapViewModel.ChartMode.*

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

        subscribeToDataChanges()
        chartMode.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            if (!radio.isChecked) return@setOnCheckedChangeListener

            when (checkedId) {
                R.id.btn1 ->
                    brainMapViewModel.changeChartMode(YOU)
                R.id.btn2 ->
                    brainMapViewModel.changeChartMode(AGE_GROUP)
                R.id.btn3 ->
                    brainMapViewModel.changeChartMode(PROFESSION)
            }
        }
    }

    private fun subscribeToDataChanges() {
        brainMapViewModel.chartMode.observe(this, Observer { mode ->
            when (mode) {
                YOU -> {
                    chartMode.check(R.id.btn1)
                    radarChart.setData(brainMapViewModel.categories,
                            brainMapViewModel.yourScore)
                }
                AGE_GROUP -> {
                    chartMode.check(R.id.btn2)
                    radarChart.setData(brainMapViewModel.categories,
                            brainMapViewModel.yourScore, brainMapViewModel.ageGroupScore)
                }
                PROFESSION -> {
                    chartMode.check(R.id.btn3)
                    radarChart.setData(brainMapViewModel.categories,
                            brainMapViewModel.yourScore, brainMapViewModel.professionScore)
                }
            }
        })
    }
}
