package com.penguino.data.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun ObserveLifecycle(lifecycleOwner: LifecycleOwner, observer: LifecycleEventObserver, final: () -> Unit = {}) {
	DisposableEffect(key1 = lifecycleOwner) {
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose {
			lifecycleOwner.lifecycle.removeObserver(observer)
			final()
		}
	}
}
