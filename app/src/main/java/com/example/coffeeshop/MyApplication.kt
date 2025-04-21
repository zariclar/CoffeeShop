package com.example.coffeeshop

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    // Hilt kurulumu için bu sınıfın boş olması yeterlidir.
    // Uygulama genelinde başlatılması gereken başka kodlarınız varsa buraya ekleyebilirsiniz.
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: MyApplication

        fun getContext(): Context = instance.applicationContext
    }
}