package com.materialmountains.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.Button
import com.materialmountains.R

class UpgradeProDialog {
    fun showDialog(mContext: Context, callback: UpgradeProDialogEvents) {
        val dialog = Dialog(mContext)
        dialog.setContentView(R.layout.dialog_upgrade_pro)

        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })

        val btnUpgradePro = dialog.findViewById<Button>(R.id.btnUpgradePro)
        btnUpgradePro.setOnClickListener(View.OnClickListener {
            callback.onUpgradeProClicked()
            dialog.dismiss()
        })


        dialog.show()
    }

    interface UpgradeProDialogEvents {
        fun onUpgradeProClicked()
    }
}