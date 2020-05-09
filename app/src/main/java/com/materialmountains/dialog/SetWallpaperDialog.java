package com.materialmountains.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.materialmountains.R;

public class SetWallpaperDialog {

    public enum ESaveWallpaperOptions {
        HomeScreen,
        LockScreen,
        HomeAndLockScreen
    }

    public static void showDialog(final Context context, final SetWallpaperDialogEvents callback) {
        final CharSequence[] items = {context.getString(R.string.home_and_lock_screens), context.getString(R.string.home_screen), context.getString(R.string.lock_screen)};
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if(items[position].equals(context.getString(R.string.home_screen))) {
                    callback.onOptionSelected(ESaveWallpaperOptions.HomeScreen);
                } else if(items[position].equals(context.getString(R.string.lock_screen))){
                    callback.onOptionSelected(ESaveWallpaperOptions.LockScreen);
                } else if(items[position].equals(context.getString(R.string.home_and_lock_screens))){
                    callback.onOptionSelected(ESaveWallpaperOptions.HomeAndLockScreen);
                }
            }
        });
        builder.setTitle(context.getString(R.string.set_as_wallpaper));
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

        builder.create().show();
    }

    public interface SetWallpaperDialogEvents {
        void onOptionSelected(ESaveWallpaperOptions option);
    }
}
