package com.materialmountains.activities

import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.materialmountains.*
import com.materialmountains.dialog.UpgradeProDialog
import com.materialmountains.fragments.LayoutPickerFragment
import com.materialmountains.live_wallpaper.LiveWallpaperService
import com.materialmountains.live_wallpaper.LiveWallpaperServiceSwitch
import com.materialmountains.utilities.Prefs
import com.materialmountains.wallpaper_layouts.ELayoutName

class MainActivity : AppCompatActivity(),
    LayoutPickerFragment.LayoutPickerEvents,
    NavigationView.OnNavigationItemSelectedListener {

    lateinit private var fragLayoutPicker: LayoutPickerFragment

    lateinit private var mDrawerToggle: ActionBarDrawerToggle

    val TAG_LAYOUT_PICKER = "tag_layout_picker"



    override fun onLayoutSelected(layoutName:String) {
        if(layoutName != ELayoutName.random_pro.name) {
                Intent(this, SelectThemeActivity::class.java).apply {
                    putExtra("layoutName", layoutName)
                    startActivity(this)
                }
        } else if(App.isPro) {
            Toast.makeText(this, getString(R.string.random_layout_changes), Toast.LENGTH_LONG).show()
            //The random layout, pro only
            PreferenceManager.getDefaultSharedPreferences(this).apply {
                edit()
                    .putString(Prefs.SELECTED_LAYOUT_NAME, ELayoutName.random_pro.name)
                    .commit()
            }

            var intent = Intent(
                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
            )

            if(!LiveWallpaperService.isLiveWallpaperAlreadySet()) {
                intent.putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this, LiveWallpaperService::class.java)
                )
            } else {
                intent.putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this, LiveWallpaperServiceSwitch::class.java)
                )
            }

            startActivity(intent)
        } else {
            UpgradeProDialog().showDialog(this,
                object : UpgradeProDialog.UpgradeProDialogEvents {
                    override fun onUpgradeProClicked() {
                        upgradeToPro()
                    }
                })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //App.isPro = true //TODO remove this

        setSupportActionBar(toolbar)
        initDrawerLayout()

        supportActionBar?.title = getString(R.string.select_a_layout)

        navView.setNavigationItemSelectedListener(this)

        if(savedInstanceState == null) {
            fragLayoutPicker = LayoutPickerFragment()

            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, fragLayoutPicker!!, TAG_LAYOUT_PICKER)
                .commit()
        } else {
            fragLayoutPicker = supportFragmentManager.findFragmentByTag(TAG_LAYOUT_PICKER) as LayoutPickerFragment
            supportFragmentManager
                .beginTransaction()
                .detach(fragLayoutPicker)
                .attach(fragLayoutPicker)
                .commit()
        }

    }

    private fun initDrawerLayout() {
        mDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.app_name, R.string.app_name
        )
        mDrawerToggle.setDrawerIndicatorEnabled(true)
        mDrawerToggle.syncState()

        drawerLayout.setScrimColor(Color.TRANSPARENT)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_pro -> UpgradeProDialog().showDialog(this,
                object : UpgradeProDialog.UpgradeProDialogEvents {
                    override fun onUpgradeProClicked() {
                        upgradeToPro()
                    }
                })
            R.id.menu_rate -> rateApp()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun upgradeToPro() {
        BillingManager(this@MainActivity, object : BillingManager.BillingEvents {
            override fun onBillingDone() {

            }

        }).startBilling()
    }

    private fun rateApp() {
        val uri = Uri.parse("market://details?id=" + packageName)
        val intentMarket = Intent(Intent.ACTION_VIEW, uri)
        intentMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try  {
            startActivity(intentMarket)
        }
        catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)))
        }

    }
}

