package com.martemyanova.brainmap

import android.content.Context
import android.graphics.Typeface

fun Typeface.setBold(): Typeface = Typeface.create(this, Typeface.BOLD)

fun Context.loadFont(fileName: String): Typeface =
        Typeface.createFromAsset(this.assets,"fonts/$fileName")