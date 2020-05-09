package com.materialmountains.fragments


import android.Manifest
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.materialmountains.App
import com.materialmountains.BillingManager
import com.materialmountains.R
import com.materialmountains.dialog.UpgradeProDialog
import com.materialmountains.live_wallpaper.LiveWallpaperService
import com.materialmountains.live_wallpaper.LiveWallpaperServiceSwitch
import com.materialmountains.utilities.Prefs
import com.materialmountains.wallpaper_layouts.VectorMountain
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup
import kotlinx.android.synthetic.main.fragment_assets.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import kotlin.random.Random


class AssetsFragment : Fragment() {

    val LAYOUT_NAME= "layout_name"
    val THEME_ID = "theme_id"

    lateinit var preview: Bitmap
    var screenWidth:Int = 0
    var screenHeight:Int = 0
    var layoutName: String = ""
    var styleId: Int = 0
    var snackBarPro:Snackbar? = null
    val EXTERNAL_STORAGE_WALLPAPER_PERMISSION_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_assets, container, false)

        view.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                if(snackBarPro != null) {
                    snackBarPro!!.dismiss()
                }
                return false
            }

        })

        return view
    }

    companion object {
        fun newInstance(layoutName:String, themeId:Int) = AssetsFragment().apply {
            arguments = Bundle().apply {
                putString(LAYOUT_NAME, layoutName)
                putInt(THEME_ID, themeId)
            }
        }
    }

    fun getScreenSize() {
        val windowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
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

    fun setButtonText() {
        if(toggleSeagulls.isChecked || toggleTrees.isChecked || layoutName!!.contains("pro") && !App.isPro) {
            btnDone.setText(R.string.done_pro)
        } else {
            btnDone.setText(R.string.done)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        layoutName = arguments!!.getString(LAYOUT_NAME).toString()
        styleId = arguments!!.getInt(THEME_ID)

        getScreenSize()
        drawPreview()
        setButtonText()

        toggleGroupAssets.setOnCheckedChangeListener{ multiSelectToggleGroup: MultiSelectToggleGroup, i: Int, b: Boolean ->
            setButtonText()
            drawPreview()
        }

        btnDone.setOnClickListener {

            if((toggleSeagulls.isChecked || toggleTrees.isChecked) && !App.isPro) {
                snackBarPro =  Snackbar
                                .make(activity!!.findViewById(android.R.id.content), "Seagulls and Trees assets are only available in the pro version.", Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.upgrade_to_pro) {
                                    UpgradeProDialog().showDialog(requireContext(), object:UpgradeProDialog.UpgradeProDialogEvents{
                                        override fun onUpgradeProClicked() {
                                            upgradeToPro()
                                        }
                                    })
                                }
                snackBarPro!!.show()
            } else if(layoutName.contains("pro") && !App.isPro) {
                snackBarPro =  Snackbar
                                .make(activity!!.findViewById(android.R.id.content), "The layout/theme you selected is only available in the pro version.", Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.upgrade_to_pro) {
                                    UpgradeProDialog().showDialog(requireContext(), object:UpgradeProDialog.UpgradeProDialogEvents{
                                        override fun onUpgradeProClicked() {
                                            upgradeToPro()
                                        }
                                    })
                                }
                snackBarPro!!.show()
            } else {
                done()

            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                EXTERNAL_STORAGE_WALLPAPER_PERMISSION_CODE -> saveImageOnDevice()
            }
        }
    }

    fun upgradeToPro() {
        BillingManager(requireActivity(), object: BillingManager.BillingEvents {
            override fun onBillingDone() {
                if(App.isPro)
                    done()
            }
        }).startBilling()
    }

    fun done() {
        val options = arrayOf(getString(R.string.set_as_wallpaper), getString(R.string.save_on_device))
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.select_an_option))
        builder.setItems(options, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if(getString(R.string.set_as_wallpaper).equals(options[which])) {
                    setImageAsLiveWallpaper()
                } else if(getString(R.string.save_on_device).equals(options[which])) {
                    if(checkExternalStoragePermission(EXTERNAL_STORAGE_WALLPAPER_PERMISSION_CODE)) {
                        saveImageOnDevice()
                    }
                }
            }
        })

        builder.show()

    }

    fun saveImageOnDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            addImageToGallery()
        } else {
            val pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            var out: FileOutputStream? = null
            try {
                val ran = Random.nextInt(99999)
                var path = "$pictureDirectory/wallpaper_$layoutName$styleId$ran.png"
                out = FileOutputStream(path)
                preview.compress(Bitmap.CompressFormat.PNG, 100, out)

                //scan library for update
                var mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                var f = File(path)
                var contentUri = Uri.fromFile(f)
                mediaScanIntent.data = contentUri
                context!!.sendBroadcast(mediaScanIntent)

                Toast.makeText(context, getString(R.string.saved_to_downloads_folder), Toast.LENGTH_LONG).show()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    fun checkExternalStoragePermission(code: Int): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    code
                )
                return false
            }
        }
        return true
    }

    fun addImageToGallery() {
        val contentValues = ContentValues().apply {
            val ran = Random.nextInt(99999)
            put(MediaStore.MediaColumns.DISPLAY_NAME, "wallpaper_$layoutName$styleId$ran.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                putNull(MediaStore.MediaColumns.RELATIVE_PATH)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }
        val resolver = context!!.contentResolver
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            ) else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val uri = resolver.insert(collection, contentValues)

        uri?.let {
            resolver.openOutputStream(it).use {
                outputStream -> preview.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            contentValues.clear()
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }

        Toast.makeText(context, getString(R.string.saved_to_picture_gallery), Toast.LENGTH_LONG).show()
    }

    fun setImageAsLiveWallpaper() {
        var hiddenStyleId = getHiddenStyle()
        if(hiddenStyleId == null) {
            hiddenStyleId = -1
        }

        PreferenceManager.getDefaultSharedPreferences(context).apply {
            edit()
                .putString(Prefs.SELECTED_LAYOUT_NAME, layoutName)
                .putInt(Prefs.SELECTED_STYLE_ID, styleId)
                .putInt(Prefs.HIDDEN_STYLE_ID, hiddenStyleId)
                .commit()
        }
        var intent = Intent(
            WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
        )

        if(!LiveWallpaperService.isLiveWallpaperAlreadySet()) {
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(requireContext(), LiveWallpaperService::class.java)
            )
        } else {
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(requireContext(), LiveWallpaperServiceSwitch::class.java)
            )
        }

        startActivity(intent)
    }

    fun drawPreview() {
        if(layoutName!!.contains("full") && context!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && !layoutName.contains("_landscape")) {
            layoutName += "_landscape"
        } else if(context!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && layoutName.contains("_landscape")) {
            layoutName = layoutName.replace("_landscape", "")
        }

        preview = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        val c = Canvas(preview)
        VectorMountain(arguments?.getInt(THEME_ID)!!, layoutName, getHiddenStyle()).draw(c)

        imgWallpaperPreview.setImageBitmap(preview)
    }

    fun getHiddenStyle():Int? {
        if(toggleClouds.isChecked && !toggleSeagulls.isChecked && !toggleTrees.isChecked) {
            return R.style.trees_seagulls_hidden
        } else if(!toggleClouds.isChecked && toggleSeagulls.isChecked && !toggleTrees.isChecked) {
            return R.style.clouds_trees_hidden
        } else if(!toggleClouds.isChecked && !toggleSeagulls.isChecked && toggleTrees.isChecked) {
            return R.style.seagulls_clouds_hidden
        } else if(!toggleClouds.isChecked && toggleSeagulls.isChecked && toggleTrees.isChecked) {
            return R.style.clouds_hidden
        } else if(toggleClouds.isChecked && !toggleSeagulls.isChecked && toggleTrees.isChecked) {
            return R.style.seagulls_hidden
        } else if(toggleClouds.isChecked && toggleSeagulls.isChecked && !toggleTrees.isChecked) {
            return R.style.trees_hidden
        } else if(!toggleClouds.isChecked && !toggleSeagulls.isChecked && !toggleTrees.isChecked) {
            return R.style.all_assets_hidden
        }

        return null

    }

}
