package com.materialmountains

import android.app.Application
import android.app.WallpaperManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.materialmountains.utilities.Prefs

class App : Application() {
    companion object {
        lateinit var context: Context private set
        var isPro: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        context = this

        PreferenceManager.getDefaultSharedPreferences(this).apply {
            isPro = getBoolean(Prefs.IS_PRO, false)
        }
    }


}