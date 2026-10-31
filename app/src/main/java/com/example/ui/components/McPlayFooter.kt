package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun McPlayFooter(
    onParentsClick: () -> Unit,
    onLegalClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Parents and Legal Links
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onParentsClick() }
                        .testTag("footer_parents_button")
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "▶ Parents",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF555555)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onLegalClick() }
                        .testTag("footer_legal_button")
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "▶ Legal",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF555555)
                    )
                }
            }

            // Right: Classic McPlay Power Milk promotional slogan
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(McPlayPeachBg.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🥛",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Column {
                    Text(
                        text = "Be powerful & fierce",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = McPlayDeepBlue
                    )
                    Text(
                        text = "with cool, refreshing milk!",
                        fontSize = 10.sp,
                        color = Color(0xFF444444)
                    )
                }
            }
        }
    }
}
