package com.martemyanova.brainmap

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ShareActionProvider
import android.view.Menu
import android.widget.RadioButton
import android.widget.TextView
import com.martemyanova.brainmap.BrainMapViewModel.ChartMode.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val brainMapViewModel by lazy {
        ViewModelProviders.of(this).get(BrainMapViewModel::class.java)
    }
    private val lightTypeface by lazy { loadFont("GothamSSm-Light.ttf") }
    private val mediumTypeface by lazy { loadFont("GothamSSm-Medium.ttf") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customizeActionBar()

        // set custom fonts
        titleText.typeface = mediumTypeface.setBold()
        descriptionText.typeface = lightTypeface
        btnYou.typeface = lightTypeface
        btnAgeGroup.typeface = lightTypeface
        btnProfession.typeface = lightTypeface
        radarChart.typeface = lightTypeface

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

    private fun customizeActionBar() {
        val actionBar = supportActionBar ?: return

        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar.setCustomView(R.layout.action_bar)
        val actionBarCaption = findViewById<TextView>(R.id.actionBarCaption)
        actionBarCaption.typeface = lightTypeface
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val item = menu.findItem(R.id.menu_item_share)
        val shareActionProvider = MenuItemCompat.getActionProvider(item) as ShareActionProvider?
        shareActionProvider?.setShareIntent(createShareIntent())

        return true
    }

    private fun createShareIntent(): Intent =
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "My score.")
            type = "text/plain"
        }
}
