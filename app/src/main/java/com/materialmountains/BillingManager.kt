package com.materialmountains

import android.app.Activity
import android.view.View
import androidx.preference.PreferenceManager
import com.android.billingclient.api.*
import com.google.android.material.snackbar.Snackbar
import com.materialmountains.utilities.Prefs
import java.util.ArrayList

open class BillingManager(var activityContext:Activity, var listener:BillingEvents) : PurchasesUpdatedListener {

    lateinit private var billingClient: BillingClient;

    companion object {
        val PRO_SKU_ID = "pro"
    }

    fun startBilling() {
        billingClient = BillingClient.newBuilder(App.context).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    val skuList = ArrayList<String>()
                    skuList.add(PRO_SKU_ID)
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(params.build(),
                        SkuDetailsResponseListener { responseCode, skuDetailsList ->
                            if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                                for (skuDetails in skuDetailsList) {
                                    val sku = skuDetails.sku
                                    val price = skuDetails.price
                                    if (sku == PRO_SKU_ID) {
                                        val flowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetails)
                                            .build()
                                        val code = billingClient.launchBillingFlow(
                                            activityContext,
                                            flowParams
                                        )
                                    }
                                }
                            }
                        })
                }
            }

            override fun onBillingServiceDisconnected() {
                Snackbar.make(
                    activityContext.findViewById<View>(android.R.id.content),
                    R.string.unable_to_connect_to_google_play,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(
                        R.string.retry,
                        View.OnClickListener { startBilling() })
                    .show()

                listener.onBillingDone()
            }
        })
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {

        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.sku == PRO_SKU_ID) {
                    setPro()
                }
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {
            setPro()
            Snackbar.make(
                activityContext.findViewById<View>(android.R.id.content),
                R.string.you_already_own_that_item_purchase_restored,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.dismiss) { }
                .show()
        } else {
            Snackbar.make(
                activityContext.findViewById<View>(android.R.id.content),
                R.string.error_unknown_with_purchase,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.retry) { startBilling()}
                .show()
        }

        listener.onBillingDone()
    }

    fun setPro() {
        PreferenceManager.getDefaultSharedPreferences(App.context).edit().putBoolean(Prefs.IS_PRO, true).apply()
        App.isPro = true
    }

    interface BillingEvents {
        fun onBillingDone()
    }
}