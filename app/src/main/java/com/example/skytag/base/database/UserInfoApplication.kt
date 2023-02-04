package com.example.skytag.base.database

import android.app.Application
import androidx.room.Room

class UserInfoApplication: Application() {
    companion object{
        lateinit var database: UserInfoDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this,
            UserInfoDatabase::class.java,
            "UserInfoDatabase")
            .build()
    }
}