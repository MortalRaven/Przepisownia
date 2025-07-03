package com.mort.przepisownia

import android.app.Application
import com.mort.przepisownia.data.Graph

class RecipesApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}