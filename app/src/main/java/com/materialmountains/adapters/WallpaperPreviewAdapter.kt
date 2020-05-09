package com.materialmountains.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.materialmountains.R
import com.materialmountains.wallpaper_layouts.VectorMountain
import kotlinx.android.synthetic.main.row_layout_and_theme.view.*
import android.view.Display
import android.view.WindowManager
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import com.materialmountains.App


class WallpaperPreviewAdapter(val items: ArrayList<VectorMountain>, val context: Context, var mListener: OnItemClickListener): RecyclerView.Adapter<WallpaperPreviewAdapter.ViewHolder>() {

    val screenWidth:Int
    val screenHeight:Int

    init {

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val screenResolution = Point()
            display.getRealSize(screenResolution)
            screenWidth = screenResolution.x
            screenHeight = screenResolution.y
        } else {
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            screenWidth = metrics.widthPixels
            screenHeight = metrics.heightPixels
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.row_layout_and_theme, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)

        items[position].apply {
            draw(c)
            holder.imgWallpaperPreview.setImageBitmap(bitmap)

            if(this.layoutName.contains("pro") && !App.isPro) {
                holder.relLayPro.visibility = View.VISIBLE
            } else {
                holder.relLayPro.visibility = View.GONE
            }

        }

        holder.itemView.setOnClickListener { v -> mListener.onItemClickListener(v, items[position], position)}
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {

        val imgWallpaperPreview = view.imgWallpaperPreview
        val relLayPro = view.relLayPro

    }

    interface OnItemClickListener {
        fun onItemClickListener(v: View, vectorMountain:VectorMountain, pos: Int)
    }
}