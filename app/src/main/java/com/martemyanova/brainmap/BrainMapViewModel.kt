package com.martemyanova.brainmap

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class BrainMapViewModel: ViewModel() {

    private var categoriesNumber = BrainMapABTesting.instance.categoriesNumber
    val categories
        get() = arrayOf("Peak Brain Score", "Memory", "Problem Solving",
                "Language", "Mental Agility", "Focus").copyOfRange(0, categoriesNumber)

    private val yourScore = arrayOf(876, 800, 600, 512, 924, 700)
    private val ageGroupScore = arrayOf(600, 898, 200, 700, 702, 910)
    private val professionScore = arrayOf(1000, 150, 1000, 150, 1000, 150)

    enum class ChartMode {
        YOU, AGE_GROUP, PROFESSION
    }
    val chartMode: MutableLiveData<ChartMode> = MutableLiveData()

    var mainShapeData = yourScore
    val secondShapeData: Array<Int>
        get() = when (chartMode.value) {
            ChartMode.AGE_GROUP -> ageGroupScore
            ChartMode.PROFESSION -> professionScore
            else -> arrayOf<Int>()
        }

    init {
        chartMode.value = ChartMode.YOU
    }

    fun changeChartMode(mode: ChartMode) {
        chartMode.value = mode
    }

}