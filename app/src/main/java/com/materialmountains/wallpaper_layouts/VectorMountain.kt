package com.materialmountains.wallpaper_layouts

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.appcompat.view.ContextThemeWrapper
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.materialmountains.App
import com.materialmountains.R
import android.util.TypedValue


class VectorMountain(var styleId:Int, var layoutName:String, var hiddenStyle:Int?) {

    val isFullscreen:Boolean

    init {
        isFullscreen = layoutName.contains("full")
    }

    fun draw(c:Canvas) {
        if(c != null) {
            var height: Float = c.height.toFloat()
            var width: Float = c.width.toFloat()

            if (App.context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if(!isFullscreen)
                    width = c.height.toFloat()
            }

            val wrapper = ContextThemeWrapper(App.context, styleId)
            if(hiddenStyle != null && hiddenStyle != -1) {
                wrapper.theme.applyStyle(hiddenStyle!!, true)
            }
            drawBackground(wrapper.theme, c)

            val vector = VectorDrawableCompat.create(
                App.context.resources,
                App.context.resources.getIdentifier(layoutName, "drawable", App.context.packageName),
                wrapper.getTheme()
            )
            var size = getScaledDimension(
                vector!!.intrinsicWidth,
                vector!!.intrinsicHeight,
                Math.round(width / 1.2f),
                Math.round(height)
            )

            if(!isFullscreen) {
                vector?.setBounds(0, 0, Math.round(size.width), Math.round(size.height))

                val translateX =
                    c.width / 2f - size.width / 2f
                val translateY =
                    c.height / 2f - size.height / 2f
                c.translate(translateX, translateY)
                vector?.draw(c)
                c.translate(-translateX, -translateY)
            } else {
                vector?.setBounds(0, 0, Math.round(width), Math.round(height))
                vector?.draw(c)
            }
        }


    }

    private fun getScaledDimension(imgWidth: Int, imgHeight:Int, boundaryWidth:Int, boundaryHeight:Int) = object {

        val widthRatio = boundaryWidth.toFloat() / imgWidth.toFloat()
        val heightRatio = boundaryHeight.toFloat() / imgHeight.toFloat()
        val ratio = Math.min(widthRatio, heightRatio)

        var width = (imgWidth * ratio)
        var height = (imgHeight * ratio)

    }

    fun drawBackground(theme: Resources.Theme, c:Canvas) {

        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.backgroundStartColor, typedValue, true)
        var colorStartId = typedValue.data

        theme.resolveAttribute(R.attr.backgroundEndColor, typedValue, true)
        var colorEndId = typedValue.data
        
        val p = Paint()
        p.isDither = true
        if(colorStartId != colorEndId) {
            p.shader = LinearGradient(
                0f,
                0f,
                0f,
                c.height.toFloat(),
                colorStartId,
                colorEndId,
                Shader.TileMode.MIRROR
            )
        } else {
            p.style = Paint.Style.FILL
            p.color = colorStartId
        }

        c.drawPaint(p)
    }


}