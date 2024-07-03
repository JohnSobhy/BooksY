package com.john_halaka.booksy.ui.presentation.book_content

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.john_halaka.booksy.BooksYApp
import com.john_halaka.booksy.R
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import com.john_halaka.booksy.feature_book_view.domain.decodeText
import com.john_halaka.booksy.feature_book_view.domain.repository.LinkRepository
import org.json.JSONObject
import java.io.File
import java.io.InputStream

@Composable
fun Demo(){
    val viewModel: BookContentViewModel = hiltViewModel()
    val context = LocalContext.current

    val inputStream: InputStream = context.resources.openRawResource(R.raw.encoding)
    val jsonText = inputStream.bufferedReader().use { it.readText() }

    val jsonConfig = JSONObject(jsonText)
    val encodingConfig = jsonConfig.getJSONObject("encoding")
    val fontsConfig = jsonConfig.getJSONObject("fonts")
    val encodedText: String = context.resources.openRawResource(R.raw.demo4).bufferedReader().use { it.readText() }

    val linkRepository: LinkRepository = viewModel.linkRepository
    val bookId: Int = 1

    val spannableStringBuilder : SpannableStringBuilder = decodeText(encodedText,encodingConfig,fontsConfig,linkRepository,bookId)
    println(spannableStringBuilder).toString()
//        AndroidView(
//            factory = { currentContext ->
//                // Inflate the XML layout
//                val view = LayoutInflater.from(currentContext)
//                    .inflate(R.layout.text_view_layout, null, false)
//
//                val textView = view.findViewById<TextView>(R.id.text_view)
//                textView.movementMethod = LinkMovementMethod.getInstance() // Enable link clicking
//
//                return@AndroidView view
//
//            },
//            update = { view ->
//                // Get the TextView from the inflated layout
//                val textView = view.findViewById<TextView>(R.id.text_view)
//                textView.text = spannableStringBuilder // Set the SpannableStringBuilder to the TextView
//            }
//
//        )
}

