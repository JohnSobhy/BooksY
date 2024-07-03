package com.john_halaka.booksy.feature_book_view.domain.bookParser

import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AlignmentSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import reader.data.Font


fun applyFontCustomizations(
    spannable: SpannableStringBuilder,
    fontTag: String,
    start: Int,
    end: Int,
    fonts: Map<String, Font>
) {
    val fontStyle = fonts[fontTag]

    if (fontStyle?.bold == "1") {
        spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    if (fontStyle?.italic == "1") {
        spannable.setSpan(StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    if (fontStyle?.underline == "1") {
        spannable.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    //center alignment
    if (fontStyle?.align == "c"){
        spannable.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    // end alignment
    else if (fontStyle?.align == "1"){
        spannable.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    // start alignment
    else if (fontStyle?.align == "0"){
        spannable.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    spannable.setSpan(
        fontStyle?.size?.let { RelativeSizeSpan(it.toFloat()) },
        start,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    spannable.setSpan(
        ForegroundColorSpan(Color.parseColor(fontStyle?.fontColor)),
        start,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    if (fontStyle?.backgroundColor != "0")
    spannable.setSpan(
        BackgroundColorSpan(Color.parseColor(fontStyle?.backgroundColor)),
        start,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )


    Log.d("ApplyFont", "Applied font style: $fontStyle from $start to $end")
}