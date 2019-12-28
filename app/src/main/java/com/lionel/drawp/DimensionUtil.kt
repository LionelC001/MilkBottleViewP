package com.lionel.drawp

import android.content.Context
import android.util.TypedValue

object DimensionUtil {
    fun turnDpToPx(context: Context, value: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), context.resources.displayMetrics
        )
    }
}
