package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun LegalDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("legal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Legal & Advertising Info",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = McPlayRed
                )

                Text(
                    text = "Hey kids and parents! McDonald's McPlay Dash & Friends is an interactive entertainment experience celebrating the 2016 Happy Meal toy series.",
                    fontSize = 13.sp,
                    color = McPlayTextDark,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = McPlayPeachBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Advertising Notice: This application contains commercial advertising and promotional content for McDonald's Happy Meal products and toys.",
                            fontSize = 12.sp,
                            color = McPlayTextDark,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Child Privacy & Safety: No personal identifiable information is gathered. No external third-party behavioral trackers or in-app payments are present.",
                            fontSize = 12.sp,
                            color = McPlayGray
                        )
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = McPlayGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I Understand", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
