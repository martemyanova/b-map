package com.martemyanova.brainmap

import android.util.Log

class BrainMapABTesting private constructor(){

    private val TAG = BrainMapABTesting::class.java.simpleName
    private val PARAM_CATEGORIES_NUMBER = "categoriesNumber"
    private val defaultCategoriesNumber = 6
    private val abTesting: ABTesting by lazy { ABTestingDummyImpl() }

    var categoriesNumber = defaultCategoriesNumber
        get() = abTesting.getLong(PARAM_CATEGORIES_NUMBER).toInt()
        private set

    fun init() {
        abTesting.setDefault(hashMapOf(
                PARAM_CATEGORIES_NUMBER to defaultCategoriesNumber.toString()))

        //doAsync
        abTesting.fetch { isSuccessful ->
            if (!isSuccessful) {
                Log.e(TAG, "Failed to fetch AB test assignments. How to handle this depends on particular app's logic")
            }
        }
    }

    companion object {
        val instance by lazy { BrainMapABTesting() }
    }

}