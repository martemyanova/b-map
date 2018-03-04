package com.martemyanova.brainmap

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_main.*
import com.martemyanova.brainmap.BrainMapViewModel.ChartMode.*

class MainActivity : AppCompatActivity() {

    private val brainMapViewModel by lazy {
        ViewModelProviders.of(this).get(BrainMapViewModel::class.java)
    }
    private val lightTypeface by lazy { loadFont("GothamSSm-Light.ttf") }
    private val mediumTypeface by lazy { loadFont("GothamSSm-Medium.ttf") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set custom fonts
        titleText.typeface = mediumTypeface.setBold()
        descriptionText.typeface = lightTypeface
        btnYou.typeface = lightTypeface
        btnAgeGroup.typeface = lightTypeface
        btnProfession.typeface = lightTypeface

        subscribeToDataChanges()
        chartMode.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            if (!radio.isChecked) return@setOnCheckedChangeListener

            when (checkedId) {
                R.id.btnYou ->
                    brainMapViewModel.changeChartMode(YOU)
                R.id.btnAgeGroup ->
                    brainMapViewModel.changeChartMode(AGE_GROUP)
                R.id.btnProfession ->
                    brainMapViewModel.changeChartMode(PROFESSION)
            }
        }
    }

    private fun subscribeToDataChanges() {
        brainMapViewModel.chartMode.observe(this, Observer { mode ->
            when (mode) {
                YOU -> {
                    chartMode.check(R.id.btnYou)
                }
                AGE_GROUP -> {
                    chartMode.check(R.id.btnAgeGroup)
                }
                PROFESSION -> {
                    chartMode.check(R.id.btnProfession)
                }
            }
            brainMapViewModel.apply {
                radarChart.setData(categories, mainShapeData, secondShapeData)
            }
        })
    }

    private fun loadFont(fileName: String): Typeface =
            Typeface.createFromAsset(this.assets,"fonts/$fileName")

    private fun Typeface.setBold(): Typeface = Typeface.create(this, Typeface.BOLD)
}
