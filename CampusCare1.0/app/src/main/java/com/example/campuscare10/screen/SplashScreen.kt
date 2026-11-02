package com.example.campuscare10.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable

fun SplashScreen(
    onNavigateToStudent: () -> Unit,
    onNavigateToStaff: () -> Unit
) {

    val primaryPurple = Color(0xFF7B43FF)
    val staffGreen = Color(0xFF388E3C)
    val bgColor = Color(0xFFF8F8FF)

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {


        Icon(

            imageVector = Icons.Default.School,
            contentDescription = "Campus Icon",
            modifier = Modifier.size(120.dp),
            tint = primaryPurple

        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(

            text = "CampusCare",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = primaryPurple

        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(

            text = "Report, Track, Improve.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black

        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(

            text = "Together, we build\na better campus.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center

        )


        Spacer(modifier = Modifier.height(48.dp))

        Button(

            onClick = onNavigateToStudent,
            modifier = Modifier
                .width(160.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
            shape = RoundedCornerShape(8.dp)

        ) {

            Text("Student", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToStaff,
            modifier = Modifier
                .width(160.dp)
                .height(48.dp),

            colors = ButtonDefaults.buttonColors(containerColor = staffGreen),
            shape = RoundedCornerShape(8.dp)

        ) {

            Text("Staff", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onNavigateToStudent = {}, onNavigateToStaff = {})

}