package com.daurenbek.encryptionapp

import android.app.Activity
import android.app.AlertDialog

class LoadingDialog {
    private lateinit var activity: Activity
    private lateinit var dialog: AlertDialog

    constructor(myActivity: Activity) {
        activity = myActivity
    }

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_dialog, null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}