package com.materialmountains.live_wallpaper

import android.app.WallpaperManager
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.preference.PreferenceManager
import com.materialmountains.App
import com.materialmountains.R
import com.materialmountains.utilities.Prefs
import com.materialmountains.wallpaper_layouts.ELayoutName
import com.materialmountains.wallpaper_layouts.VectorMountain
import com.materialmountains.wallpaper_themes.EThemeName
import kotlin.random.Random


open class LiveWallpaperService : WallpaperService() {

    companion object {
        fun isLiveWallpaperAlreadySet() : Boolean {
            var wpm = WallpaperManager.getInstance(App.context)
            var wpi = wpm.wallpaperInfo
            if (wpi != null && wpi.serviceName == "com.materialmountains.live_wallpaper.LiveWallpaperService") {
                return true
            }

            return false
        }
    }

    override fun onCreateEngine(): Engine {
        return LiveWallpaperEngine()
    }

    private inner class LiveWallpaperEngine : Engine() {
        private var visible = true
        private val handler = Handler()
        private var FPS: Int = 0
        private var styleId: Int
        private var layoutName: String
        private var hiddenStyleId: Int
        private var transitionTime:Int = 30
        private var isRandom:Boolean = false
        private var shouldUpdateRandom = true

        private val drawRunner = Runnable { draw() }

        private val randomDrawRunner = Runnable { drawRandom() }

        private val shouldUpdateRandomRunner = Runnable { shouldUpdateRandom() }

        init {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this@LiveWallpaperService)

            styleId = prefs.getInt(Prefs.SELECTED_STYLE_ID, R.style.blue_night_theme)
            layoutName = prefs.getString(Prefs.SELECTED_LAYOUT_NAME, ELayoutName.five_mountains.name).toString()
            hiddenStyleId = prefs.getInt(Prefs.HIDDEN_STYLE_ID, R.style.all_assets_hidden)

            FPS = 1000 / 1 //1 fps

            if(layoutName != ELayoutName.random_pro.name) {
                isRandom = false
                handler.post(drawRunner)
            } else {
                isRandom = true
                shouldUpdateRandom()
                handler.post(randomDrawRunner)
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                if(!isRandom)
                    handler.post(drawRunner)
                else
                    handler.post(randomDrawRunner)
            } else {
                if(!isRandom)
                    handler.removeCallbacks(drawRunner)
                else
                    handler.removeCallbacks(randomDrawRunner)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            this.visible = false
            if(!isRandom)
                handler.removeCallbacks(drawRunner)
            else
                handler.removeCallbacks(randomDrawRunner)
        }

        fun shouldUpdateRandom() {
            shouldUpdateRandom = true
            handler.postDelayed(shouldUpdateRandomRunner, transitionTime * 1000L)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
        }


        fun drawRandom() {
            val holder = surfaceHolder
            holder.setFormat(PixelFormat.RGBA_8888)
            var c: Canvas? = null

            c = holder.lockCanvas()
            if(c != null) {
                if(shouldUpdateRandom) {
                    shouldUpdateRandom = false;
                    do{
                        layoutName =
                            ELayoutName.values().get(Random.nextInt(ELayoutName.values().size)).name
                    } while(layoutName == ELayoutName.random_pro.name)
                    var themeName =
                        EThemeName.values().get(Random.nextInt(EThemeName.values().size)).name

                    styleId = resources.getIdentifier(
                        themeName, "style",
                        App.context?.packageName
                    )

                    if (layoutName.contains("full") && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && !layoutName.contains(
                            "_landscape"
                        )
                    ) {
                        layoutName += "_landscape"
                    } else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && layoutName.contains(
                            "_landscape"
                        )
                    ) {
                        layoutName = layoutName.replace("_landscape", "")
                    }
                }
                VectorMountain(styleId, layoutName, hiddenStyleId).draw(c)
            }

            if (c != null)
                holder.unlockCanvasAndPost(c)

            handler.removeCallbacks(randomDrawRunner)
            if (visible) {
                handler.postDelayed(randomDrawRunner, FPS.toLong())
            }


        }

        fun draw() {
            val holder = surfaceHolder
            holder.setFormat(PixelFormat.RGBA_8888)
            var c: Canvas? = null

            c = holder.lockCanvas()
            if(c != null) {
                if(layoutName.contains("full") && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && !layoutName.contains("_landscape")) {
                    layoutName += "_landscape"
                } else if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && layoutName.contains("_landscape")) {
                    layoutName = layoutName.replace("_landscape", "")
                }
                VectorMountain(styleId, layoutName, hiddenStyleId).draw(c)
            }

            if (c != null)
                holder.unlockCanvasAndPost(c)

            handler.removeCallbacks(drawRunner)
            if (visible) {
                handler.postDelayed(drawRunner, FPS.toLong())
            }
        }

        
    }
}