package com.herald.moviestask.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp


@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    fontSize: TextUnit,
    iconSize: Float = 24f,
    contentDescription: String = ""
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = contentDescription,Modifier.size(iconSize.dp))
        Text(text = text, modifier = Modifier.padding(start = 5.dp), fontSize = fontSize)
    }
}