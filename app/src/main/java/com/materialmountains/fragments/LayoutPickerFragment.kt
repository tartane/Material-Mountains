package com.materialmountains.fragments


import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.materialmountains.*
import com.materialmountains.adapters.WallpaperPreviewAdapter
import com.materialmountains.item_decorators.GridSpacingItemDecoration
import com.materialmountains.item_decorators.HeaderDecoration
import com.materialmountains.utilities.PixelUtils
import com.materialmountains.wallpaper_layouts.*
import com.materialmountains.wallpaper_themes.EThemeName
import kotlinx.android.synthetic.main.fragment_layout_picker.*


class LayoutPickerFragment : Fragment() {

    lateinit var listener : LayoutPickerEvents

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layout_picker, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        drawImages()
    }

    fun drawImages() {
        var list = ArrayList<VectorMountain>()

        ELayoutName.values().forEach {
            var layoutName = it.name
            if(layoutName.contains("full") && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && !layoutName.contains("_landscape")) {
                layoutName += "_landscape"
            } else if(context!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && layoutName.contains("_landscape")) {
                layoutName = layoutName.replace("_landscape", "")
            }
            resources.getIdentifier(
                layoutName, "drawable",
                context?.getPackageName()
            ).apply {
                list.add(VectorMountain(R.style.blue_winter_theme, layoutName, R.style.all_assets_hidden))
            }
        }



        WallpaperPreviewAdapter(
            list,
            App.context,
            object :
                WallpaperPreviewAdapter.OnItemClickListener {
                override fun onItemClickListener(v: View, vectorMountain:VectorMountain, pos: Int) {
                    listener.onLayoutSelected(vectorMountain.layoutName)
                }
            }).apply {
            val spanCount = 2
            rvLayouts.layoutManager = GridLayoutManager(context, spanCount)
            rvLayouts.addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount,
                    PixelUtils.getPixelsFromDp(context, 10),
                    true
                )
            )
            /*
            var rvLayoutHeaderView = layoutInflater.inflate(R.layout.layout_picker_header, null)
            rvLayouts.addItemDecoration(
                HeaderDecoration(
                    rvLayoutHeaderView,
                    false,
                    1f,
                    0f,
                    spanCount
                )
            )*/
            rvLayouts.adapter = this
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is LayoutPickerEvents) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement LayoutPickerEvent")
        }
    }

    interface LayoutPickerEvents {
        fun onLayoutSelected(layoutName:String)
    }
}
