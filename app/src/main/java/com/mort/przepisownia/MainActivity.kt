package com.mort.przepisownia

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.mort.przepisownia.data.preferences.DataStore
import com.mort.przepisownia.data.repository.SettingsRepository
import com.mort.przepisownia.navigation.Navigation
import com.mort.przepisownia.ui.theme.PrzepisowniaTheme



class MainActivity : AppCompatActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsRepo = SettingsRepository(dataStore = DataStore(applicationContext))
        //enableEdgeToEdge()
        setContent {
            PrzepisowniaTheme(themeRepo = settingsRepo, dynamicColor = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Navigation()
                }
            }
        }
    }
}