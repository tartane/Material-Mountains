package com.materialmountains.fragments


import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.materialmountains.*
import com.materialmountains.adapters.WallpaperPreviewAdapter
import com.materialmountains.adapters.WallpaperPreviewAdapter.OnItemClickListener
import com.materialmountains.item_decorators.GridSpacingItemDecoration
import com.materialmountains.item_decorators.HeaderDecoration
import com.materialmountains.utilities.PixelUtils
import com.materialmountains.wallpaper_layouts.*
import com.materialmountains.wallpaper_themes.EThemeName
import kotlinx.android.synthetic.main.fragment_theme_picker.*
import java.util.*
import kotlin.collections.ArrayList

class ThemePickerFragment : Fragment() {

    val LAYOUT_NAME= "layout_name"

    lateinit var listener: ThemePickerEvents

    companion object {
        fun newInstance(layoutName:String) = ThemePickerFragment().apply {
            arguments = Bundle().apply {
                putString(LAYOUT_NAME, layoutName)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_theme_picker, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        drawImages()
    }


    fun drawImages() {
        var list = ArrayList<VectorMountain>()


        var layoutName = arguments!!.getString(LAYOUT_NAME)
        if(layoutName!!.contains("full") && context!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && !layoutName.contains("_landscape")) {
            layoutName += "_landscape"
        } else if(context!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && layoutName.contains("_landscape")) {
            layoutName = layoutName.replace("_landscape", "")
        }

        EThemeName.values().forEach {
            resources.getIdentifier(
                it.name, "style",
                context?.packageName
            ).apply {
                list.add(VectorMountain(this, layoutName, R.style.all_assets_hidden))
            }
        }

        WallpaperPreviewAdapter(
            list,
            App.context,
            object : OnItemClickListener {
                override fun onItemClickListener(v: View, vectorMountain: VectorMountain, pos: Int) {
                    listener.onThemeSelected(vectorMountain.styleId, vectorMountain.layoutName)
                }
            }).apply {
            val spanCount = 2
            rvThemes.layoutManager = GridLayoutManager(context, spanCount)
            rvThemes.addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount,
                    PixelUtils.getPixelsFromDp(context, 10),
                    true
                )
            )
            /*
            var rvThemeHeaderView = layoutInflater.inflate(R.layout.theme_picker_header, null)
            rvThemes.addItemDecoration(
                HeaderDecoration(
                    rvThemeHeaderView,
                    false,
                    1f,
                    0f,
                    spanCount
                )
            )*/
            rvThemes.adapter = this
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ThemePickerEvents) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement ThemePickerEvents")
        }
    }

    interface ThemePickerEvents {
        fun onThemeSelected(styleId:Int, layoutName:String)
    }
}
