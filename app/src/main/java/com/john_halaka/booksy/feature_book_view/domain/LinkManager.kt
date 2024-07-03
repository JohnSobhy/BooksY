package com.john_halaka.booksy.feature_book_view.domain

import LinkType
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import com.john_halaka.booksy.feature_book_view.domain.model.LinkSource
import com.john_halaka.booksy.feature_book_view.domain.model.LinkTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun applyLinkCustomizations(
    context: Context,
    spannable: SpannableStringBuilder,
    linkType: LinkType,
    key: String,
    start: Int,
    end: Int,
    viewModel: BookContentViewModel,
    navigationCallback: NavigationCallback
) {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            when (linkType) {
                LinkType.WEB -> {
                    openWebLink(context, key)
                    Log.d("ApplyLink", "web link clicked: $linkType from $start to $end")
                }

                LinkType.INTERNAL -> {
                    Log.d("ApplyLink", "internal link clicked: $linkType key $key")
                    navigateToInternalLink(
                        viewModel,
                        key,
                        navigationCallback
                    )
                }

                LinkType.TARGET -> {
                    // Handle target link if needed
                }
            }
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = Color.BLUE
            ds.isUnderlineText = true
        }
    }

    spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    Log.d("ApplyLink", "Applied link: $linkType from $start to $end")
}


fun openWebLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    val resolvedActivity = intent.resolveActivity(context.packageManager)
    Log.d("OpenWebLink", "Resolved Activity: $resolvedActivity") // Log the resolved activity
    if (resolvedActivity != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No application can handle this request. ", Toast.LENGTH_LONG)
            .show()
    }
}


fun navigateToInternalLink(
    viewModel: BookContentViewModel,
    tag: String,
    navigationCallback: NavigationCallback
) {
    // Implement logic to navigate to internal link
    viewModel.viewModelScope.launch(Dispatchers.IO) {
        val target = viewModel.linkRepository.findTargetByKey(1, tag)
        Log.d("navigateToInternalLink", "tag: $tag")
        // Navigate to the target on the main thread
        withContext(Dispatchers.Main) {
            target?.let {
                navigationCallback.navigateTo(it.destination) // Trigger the callback            }
            }
        }
    }
}

fun saveLinkTarget(
    viewModel: BookContentViewModel,
    tag: String,
    destination: Int
){
    viewModel.viewModelScope.launch(Dispatchers.IO) {
        viewModel.linkRepository.insertTarget(
            LinkTarget(
                tag = tag,
                destination = destination,
                bookId = 1
            )
        )
    }

}
fun saveLinkSource(
    viewModel: BookContentViewModel,
    start: Int,
    end: Int,
    tag: String
){
    viewModel.viewModelScope.launch {
        viewModel.linkRepository.insertSource(
            LinkSource(
                start = start + 2,
                end = end,
                tag = tag,
                bookId = 1
            )
        )
    }
}