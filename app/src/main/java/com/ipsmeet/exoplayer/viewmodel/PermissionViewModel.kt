package com.ipsmeet.exoplayer.viewmodel

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel

class PermissionViewModel: ViewModel() {

     fun requestPermission(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // system-default permission request permission dialog
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_MEDIA_VIDEO",
                    "android.permission.READ_MEDIA_AUDIO"
                ), 1)
            return
        }
    }

}