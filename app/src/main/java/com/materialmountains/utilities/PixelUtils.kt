package com.materialmountains.utilities

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue


object PixelUtils {

    /**
     * Convert Density pixels to normal pixels
     *
     * @param context Context
     * @param dp      Density pixels
     * @return Integer
     */
    fun getPixelsFromDp(context: Context, dp: Int): Int {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        ).toInt()
    }

    fun getPixelsFromSp(context: Context, sp: Int): Int {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), r.displayMetrics)
            .toInt()
    }

    fun getDPFromPixels(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }
}