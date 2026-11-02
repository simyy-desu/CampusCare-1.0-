package com.example.campuscare10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.nav.AppNavGraph
import com.example.campuscare10.ui.theme.CampusCare10Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CampusCare10Theme {
                val navController = rememberNavController()
                val reports = remember { mutableStateListOf<StaffReport>() }

                // Ensure AppNavGraph is called here!
                AppNavGraph(
                    navController = navController,
                    reports = reports
                )
            }
        }
    }
}
