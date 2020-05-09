package com.materialmountains.activities

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.materialmountains.R
import com.materialmountains.fragments.ThemePickerFragment
import com.materialmountains.wallpaper_layouts.ELayoutName
import kotlinx.android.synthetic.main.toolbar.*

class SelectThemeActivity : AppCompatActivity(), ThemePickerFragment.ThemePickerEvents {

    lateinit var fragThemePicker: ThemePickerFragment

    val TAG_THEME_PICKER = "tag_theme_picker"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_theme)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.select_a_theme)

        var layoutName = intent.getStringExtra("layoutName")

        if(savedInstanceState == null) {
            fragThemePicker = ThemePickerFragment.newInstance(layoutName)

            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                .add(R.id.container, fragThemePicker, TAG_THEME_PICKER)
                .commit()
        } else {
            fragThemePicker = supportFragmentManager.findFragmentByTag(TAG_THEME_PICKER) as ThemePickerFragment
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onThemeSelected(styleId:Int, layoutName:String) {

        Intent(this, AssetsActivity::class.java).apply {
            putExtra("layout_name", layoutName)
            putExtra("style_id", styleId)
            startActivity(this)
        }
/*
        fragAssetsPicker = AssetsFragment.newInstance(layoutId, styleId)
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.container, fragAssetsPicker!!, TAG_ASSETS_PICKER)
            .addToBackStack(null)
            .commit()*/

        /*
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs
            .edit()
            .putInt(Prefs.SELECTED_STYLE_ID, styleId)
            .putInt(Prefs.SELECTED_LAYOUT_ID, layoutId)
            .putInt(Prefs.HIDDEN_STYLE_ID, R.style.all_assets_hidden)
            .apply()

        val intent = Intent(
            WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
        )

        val wpm = WallpaperManager.getInstance(applicationContext)
        if(wpm.wallpaperInfo != null &&
            wpm.wallpaperInfo.component.className == LiveWallpaperService::class.java.name) {
            //wallpaper is already set
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@MainActivity, LiveWallpaperServiceSwitch::class.java.name)
            )
        } else {
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@MainActivity, LiveWallpaperService::class.java.name)
            )
        }

        startActivity(intent)*/
    }
}
