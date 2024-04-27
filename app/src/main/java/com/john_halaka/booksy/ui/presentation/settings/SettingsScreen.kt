package com.john_halaka.booksy.ui.presentation.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.john_halaka.booksy.R
import com.john_halaka.booksy.feature_book.data.PreferencesManager
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import com.john_halaka.booksy.ui.presentation.book_content.BookContentEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: BookContentViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is BookContentViewModel.UiEvent.NavigateBack -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row {
                        Icon(Icons.Default.Settings, contentDescription = "Settings icon")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Settings")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onEvent(BookContentEvent.BackButtonClick)
                    })
                    {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) { values ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(values)
        ) {
            item {
                FontSizeSetting(viewModel = viewModel)
                FontColorSetting(viewModel = viewModel)
                BackgroundColorSetting(viewModel = viewModel)
                FontWeightSetting(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FontSizeSetting(
    viewModel: BookContentViewModel
) {
    val fontSize by viewModel.fontSize.collectAsState()
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Font Size", fontWeight = FontWeight.Bold)
        Slider(
            value = fontSize,
            onValueChange = { newFontSize ->
                viewModel.saveFontSize(newFontSize)
            },
            valueRange = 12f..42f,
            steps = 24
        )
        Text("Current Font Size: ${fontSize.toInt()} sp")
    }
}
@Composable
fun FontColorSetting(
    viewModel: BookContentViewModel
) {
    val fontColor by viewModel.fontColor.collectAsState()

    val availableFontColors = listOf(
        Color.Black,
        Color.Gray,
        Color.DarkGray,
        Color.Red,
        Color.Blue,
        Color.Green
        // Add more colors as needed
    )

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Font Color", fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            availableFontColors.forEach { color ->
                ColorOptionButton(
                    color = color,
                    isSelected = fontColor == color.toArgb(),
                    onClick = {
                        viewModel.saveFontColor(color.toArgb())

                    }
                )
            }
        }
    }
}
@Composable
fun ColorOptionButton(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color)
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}


@Composable
fun BackgroundColorSetting(
    viewModel: BookContentViewModel
) {
    val backgroundColor by viewModel.backgroundColor.collectAsState()
    val availableBackgroundColors = listOf(
        Color.White, // Classic white background
        Color(0xFFCFCDCD), // Light gray background
        Color(0xFFEBD69F), // Beige background
        Color(0xFFE6FAE8) // Linen background
        // Add more colors as needed
    )

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Background Color", fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            availableBackgroundColors.forEach { color ->
                ColorOptionButton(
                    color = color,
                    isSelected = backgroundColor == color.toArgb(),
                    onClick = {
                        viewModel.saveBackgroundColor(color.toArgb())
                    }
                )
            }
        }
    }
}

@Composable
fun FontWeightSetting(
    viewModel: BookContentViewModel
) {

    val savedFontWeight by viewModel.fontWeight.collectAsState()
    var selectedFontWeight: FontWeight by remember { mutableStateOf(FontWeight(savedFontWeight)) }
    val fontSize by viewModel.fontSize.collectAsState()
    val fontColor by viewModel.fontColor.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Font Weight Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Font weight options

        FontWeightOption(
            label = "Normal",
            fontWeight = FontWeight.Normal,
            selectedFontWeight= selectedFontWeight,
            onFontWeightSelected = { selectedFontWeight = it }
        )
        FontWeightOption(
            label = "Bold",
            fontWeight = FontWeight.Bold,
            selectedFontWeight= selectedFontWeight,
            onFontWeightSelected = { selectedFontWeight = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Font weight preview
        Box (
            Modifier
                .fillMaxWidth()
                .background(Color(backgroundColor))) {
            Text(
                text = "Preview Text",
                fontFamily = FontFamily.SansSerif,
                fontWeight = selectedFontWeight,
                color = Color(fontColor),
                fontSize = fontSize.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = { viewModel.saveFontWeight(selectedFontWeight.weight) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Font Weight")
        }
    }
}
@Composable
fun FontWeightOption(
    label: String,
    fontWeight: FontWeight,
    selectedFontWeight: FontWeight,
    onFontWeightSelected: (FontWeight) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFontWeightSelected(fontWeight) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selectedFontWeight == fontWeight,
            onClick = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontWeight = fontWeight)
    }
}

