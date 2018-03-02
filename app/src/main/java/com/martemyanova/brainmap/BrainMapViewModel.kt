package com.martemyanova.brainmap

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class BrainMapViewModel: ViewModel() {

    val categories = arrayOf("Peak Brain Score", "Memory", "Problem Solving",
            "Language", "Mental Agility", "Focus")

    val yourScore = arrayOf(876, 800, 600, 512, 924, 700)
    val ageGroupScore = arrayOf(600, 898, 200, 700, 702, 910)
    val professionScore = arrayOf(300, 1000, 300, 1000, 300, 1000)

    enum class ChartMode {
        YOU, AGE_GROUP, PROFESSION
    }

    val chartMode: MutableLiveData<ChartMode> = MutableLiveData()

    init {
        chartMode.value = ChartMode.YOU
    }

    fun changeChartMode(mode: ChartMode) {
        chartMode.value = mode
    }

}