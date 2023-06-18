package com.penguino.data.repositories

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// TODO: TODO TODO DODODODO
class PermissionsRepository(private val ctx: Context, private val activity: Activity) {
	fun checkPermissions(permission: String): Boolean {
		return ActivityCompat.checkSelfPermission(ctx, permission) == PERMISSION_GRANTED
	}

	fun requestPermission() {
		ActivityCompat
			.shouldShowRequestPermissionRationale(activity, "")
	}
}