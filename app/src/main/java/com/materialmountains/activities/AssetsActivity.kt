package com.materialmountains.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.materialmountains.R
import com.materialmountains.fragments.AssetsFragment
import com.materialmountains.wallpaper_layouts.ELayoutName
import kotlinx.android.synthetic.main.toolbar.*

class AssetsActivity : AppCompatActivity() {

    lateinit var fragAssets : AssetsFragment

    val TAG_ASSETS = "tag_assets"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assets)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.add_image_assets)

        var layoutName = intent.getStringExtra("layout_name")
        var styleId = intent.getIntExtra("style_id", R.style.blue_night_theme)

        if(savedInstanceState == null) {
            fragAssets = AssetsFragment.newInstance(layoutName, styleId)

            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                .add(R.id.container, fragAssets, TAG_ASSETS)
                .commit()
        } else {
            fragAssets = supportFragmentManager.findFragmentByTag(TAG_ASSETS) as AssetsFragment
            supportFragmentManager
                .beginTransaction()
                .detach(fragAssets)
                .attach(fragAssets)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

}
