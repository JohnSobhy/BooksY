package com.john_halaka.booksy.ui.presentation.settings

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
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.john_halaka.booksy.R
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import com.john_halaka.booksy.ui.presentation.book_content.BookContentEvent
import com.john_halaka.booksy.ui.presentation.book_content.showToast
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
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_icon)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.settings))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onEvent(BookContentEvent.BackButtonClick)
                    })
                    {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
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
        Text(
            stringResource(id = R.string.font_size),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Slider(
            value = fontSize,
            onValueChange = { newFontSize ->
                viewModel.saveFontSize(newFontSize)
            },
            valueRange = 12f..42f,
            steps = 24
        )
        Text(stringResource(R.string.current_font_size_sp, fontSize.toInt()))
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
        Text(stringResource(R.string.font_color), fontWeight = FontWeight.Bold, fontSize = 24.sp)
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
        Text(
            stringResource(R.string.background_color),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
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
    val context = LocalContext.current
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
        Text(
            stringResource(R.string.font_weight_settings),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Font weight options

        FontWeightOption(
            label = stringResource(R.string.normal),
            fontWeight = FontWeight.Normal,
            selectedFontWeight = selectedFontWeight,
            onFontWeightSelected = { selectedFontWeight = it }
        )
        FontWeightOption(
            label = stringResource(R.string.bold),
            fontWeight = FontWeight.Bold,
            selectedFontWeight = selectedFontWeight,
            onFontWeightSelected = { selectedFontWeight = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Font weight preview
        Box(
            Modifier
                .fillMaxWidth()
                .background(Color(backgroundColor))
        ) {
            Text(
                text = stringResource(R.string.preview_text),
                fontFamily = FontFamily.SansSerif,
                fontWeight = selectedFontWeight,
                color = Color(fontColor),
                fontSize = fontSize.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = {
                viewModel.saveFontWeight(selectedFontWeight.weight)
                showToast(context = context, context.getString(R.string.font_weight_saved))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_font_weight), fontSize = 24.sp)
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

