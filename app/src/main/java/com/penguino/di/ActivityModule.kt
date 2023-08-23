package com.penguino.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ActivityModule {
	companion object {
		@Provides
		fun context(@ApplicationContext ctx: Context): Context = ctx

		@Provides
		fun sharedPreferences(@ApplicationContext ctx: Context): SharedPreferences {
			return ctx.getSharedPreferences("PENGUINO_PREFS", Context.MODE_PRIVATE)
		}
	}

//	@Binds
//	abstract fun notificationTool(notificationTool: NotificationTool): NotificationTool
}