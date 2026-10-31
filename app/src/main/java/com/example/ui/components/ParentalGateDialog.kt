package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlin.random.Random

@Composable
fun ParentalGateDialog(
    onDismiss: () -> Unit,
    onUnlockAllToys: () -> Unit,
    onSuccess: () -> Unit
) {
    var num1 by remember { mutableIntStateOf(Random.nextInt(5, 12)) }
    var num2 by remember { mutableIntStateOf(Random.nextInt(4, 10)) }
    val correctAnswer = num1 + num2

    var isUnlocked by remember { mutableStateOf(false) }
    var inputAnswer by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("parental_gate_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (!isUnlocked) "Parents Only" else "Parental Control Center",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = McPlayRed
                )

                if (!isUnlocked) {
                    Text(
                        text = "To access the grown-up section, please solve this problem:",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = McPlayTextDark
                    )

                    Surface(
                        color = McPlayPeachBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "$num1 + $num2 = ?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = McPlayTextDark,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }

                    OutlinedTextField(
                        value = inputAnswer,
                        onValueChange = { inputAnswer = it.filter { char -> char.isDigit() } },
                        label = { Text("Enter answer") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parental_math_input")
                    )

                    errorMessage?.let {
                        Text(text = it, color = McPlayRed, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (inputAnswer.toIntOrNull() == correctAnswer) {
                                    isUnlocked = true
                                    errorMessage = null
                                } else {
                                    errorMessage = "Incorrect, please try again."
                                    num1 = Random.nextInt(5, 12)
                                    num2 = Random.nextInt(4, 10)
                                    inputAnswer = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = McPlayGreen),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("parental_submit_button")
                        ) {
                            Text("Continue", color = Color.White)
                        }
                    }
                } else {
                    // Unlocked Parent Options
                    Text(
                        text = "Manage your kid's McDonald's Dash & Friends McPlay toy collection:",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = McPlayTextDark
                    )

                    Button(
                        onClick = {
                            onUnlockAllToys()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = McPlayYellow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("unlock_all_toys_button")
                    ) {
                        Text("✨ Unlock All 8 Toys for My Kid", color = McPlayTextDark, fontWeight = FontWeight.Bold)
                    }

                    Divider()

                    Text(
                        text = "Advertising Notice: This application is a promotional experience featuring McDonald's Happy Meal Dash & Friends character toys. It does not contain in-app purchases or third-party behavioral ads.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = McPlayRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done", color = Color.White)
                    }
                }
            }
        }
    }
}
