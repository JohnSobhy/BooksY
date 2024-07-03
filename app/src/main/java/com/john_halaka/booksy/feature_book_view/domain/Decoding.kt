package com.john_halaka.booksy.feature_book_view.domain

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import com.john_halaka.booksy.feature_book_view.domain.model.LinkSource
import com.john_halaka.booksy.feature_book_view.domain.model.LinkTarget
import com.john_halaka.booksy.feature_book_view.domain.repository.LinkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

fun decodeText(
    encodedText: String,
    encodingConfig: JSONObject,
    fontsConfig: JSONObject,
    linkRepository: LinkRepository,
    bookId: Int
): SpannableStringBuilder {
    val decodedText = SpannableStringBuilder()
    var i = 0
    while (i < encodedText.length) {
        if (encodedText.substring(
                i,
                i + encodingConfig.getInt("TAG_LENGTH")
            ) == encodingConfig.getString("TAG_START")
        ) {
            // We've encountered a tag
            val formatKey = encodedText[i + encodingConfig.getInt("TAG_LENGTH")].toString()
            val formatConfig = fontsConfig.getJSONObject(formatKey)
            val tagEndIndex = encodedText.indexOf(encodingConfig.getString("TAG_END"), i)
            val taggedText = encodedText.substring(
                i + encodingConfig.getInt("TAG_LENGTH") + encodingConfig.getInt("FORMAT_LENGTH"),
                tagEndIndex
            )
            val spanStart = decodedText.length
            decodedText.append(taggedText)
            val spanEnd = decodedText.length
            // Apply the style to the tagged text
            if (formatConfig.getString("bold") == "1") {
                decodedText.setSpan(
                    StyleSpan(Typeface.BOLD),
                    spanStart,
                    spanEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (formatConfig.getString("italic") == "1") {
                decodedText.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    spanStart,
                    spanEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (formatConfig.has("size")) {
                decodedText.setSpan(
                    AbsoluteSizeSpan(formatConfig.getString("size").toInt()),
                    spanStart,
                    spanEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (formatConfig.has("font_color")) {
                val color = Color.parseColor(formatConfig.getString("font_color"))
                decodedText.setSpan(
                    ForegroundColorSpan(color),
                    spanStart,
                    spanEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (formatConfig.has("backgroundColor")) {
                val color = Color.parseColor(formatConfig.getString("backgroundColor"))
                decodedText.setSpan(
                    BackgroundColorSpan(color),
                    spanStart,
                    spanEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (formatConfig.has("under_line") && formatConfig.getString("under_line") == "1") {
                decodedText.setSpan(
                    UnderlineSpan(),
                    spanStart,
                    spanEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            // Handle links
            if (formatKey == encodingConfig.getString("INTERNAL_LINK")) {
                val linkKey = taggedText.substring(0, encodingConfig.getInt("LINK_KEY_LENGTH"))
                val linkSource =
                    LinkSource(start = spanStart, end = spanEnd, tag = linkKey, bookId = bookId)
                CoroutineScope(Dispatchers.IO).launch {
                    linkRepository.insertSource(linkSource)
                }
            } else if (formatKey == encodingConfig.getString("INTERNAL_LINK_TARGET")) {
                val linkKey = taggedText.substring(0, encodingConfig.getInt("LINK_KEY_LENGTH"))
                val linkTarget = LinkTarget(tag = linkKey, destination = spanStart, bookId = bookId)
                CoroutineScope(Dispatchers.IO).launch {
                    linkRepository.insertTarget(linkTarget)
                }
            }
            i = tagEndIndex + encodingConfig.getInt("TAG_LENGTH")
        } else {
            // This is normal text, just append it to the decoded text
            decodedText.append(encodedText[i])
            i++
        }
    }
    return decodedText
}


