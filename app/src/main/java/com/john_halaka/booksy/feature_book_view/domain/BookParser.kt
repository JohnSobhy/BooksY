import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.john_halaka.booksy.R
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import com.john_halaka.booksy.feature_book_view.domain.NavigationCallback
import com.john_halaka.booksy.feature_book_view.domain.applyLinkCustomizations
import com.john_halaka.booksy.feature_book_view.domain.saveLinkSource
import com.john_halaka.booksy.feature_book_view.domain.saveLinkTarget
import kotlinx.coroutines.launch
import reader.data.Book
import reader.data.Encoding


var KEY_LENGTH = 1
fun parseBookText(book:Book,  viewModel: BookContentViewModel): List<ParsedElement> {
    val elements = mutableListOf<ParsedElement>()
    val regex = Regex("##(?!l)(.*?)\\*\\*|##l(\\w+)", RegexOption.DOT_MATCHES_ALL)
    var lastIndex = 0

    val bookText = book.body.string
    val encoding = book.encoding
    val tagEncoding = book.encoding.tag
    val tagFonts = book.encoding.fonts
    val bookImages = book.images


    regex.findAll(bookText).forEach { matchResult ->
        val start = matchResult.range.first
        val end = matchResult.range.last + 1
        val matchedText = matchResult.value

        if (matchResult.groups[1] != null) {
            // Handle the first pattern (##(?!l)(.*?)**)
            // Add the tag content
            val tagContent = matchResult.groupValues[1]
            elements.add(parseTag(tagContent, viewModel, start, end))

        } else if (matchResult.groups[2] != null) {
            // Handle the second pattern (##l(\\w+))
            // handles target link
            val targetLinkKey = matchResult.groups[2]?.value
            Log.d("bookparse ", "TargetKey: $targetLinkKey")
            elements.add(parseTag(start, targetLinkKey!!, viewModel))
        }
        // Add normal text before the tag
        // it takes the text starting from the last index ( the index after the last tag) until the first next tag
        if (start > lastIndex) {
            elements.add(ParsedElement.Text(bookText.substring(lastIndex, start)))
        }

        lastIndex = end
    }

    // Add remaining text after the last tag
    if (lastIndex < bookText.length) {
        elements.add(ParsedElement.Text(bookText.substring(lastIndex)))
    }
    return elements
}

// this overloading is used for adding link targets to the database
fun parseTag(start: Int, key: String, viewModel: BookContentViewModel): ParsedElement {
    viewModel.viewModelScope.launch {
        saveLinkTarget(viewModel, key, start)
    }
    return ParsedElement.InternalLinkTarget(start, key)
}

fun parseTag(
    tagContent: String,
    viewModel: BookContentViewModel,
    encoding: Encoding,
    start: Int,
    end: Int,

    ): ParsedElement {
    val tagStart = tagContent.substring(0, 1)

    val content = tagContent.substring(1)
    val tags = encoding.tag
    Log.d("parseTag", "Tag: $tagStart, Content: $content")

    return when (tagStart) {
        tags.webLink -> ParsedElement.WebLink(content)
        tags.internalLink-> {
            val linkKey = content.substring(0, tags.linkKeyLength.toInt())
            val linkContent = content.substring(tags.linkKeyLength.toInt())

            saveLinkSource(viewModel, start, end, linkKey)
            ParsedElement.InternalLinkSource(linkContent, LinkType.INTERNAL, linkKey)
        }

        tags.image -> ParsedElement.Image(content)
        else -> ParsedElement.Font(content, tagStart)
    }
}


fun displayBookContent(
    context: Context,
    bookText: String,
    textView: TextView,
    viewModel: BookContentViewModel,
    navigationCallback: NavigationCallback
) {
    val elements = parseBookText(bookText, viewModel)
    val spannableStringBuilder = SpannableStringBuilder()
    val inputStream = context.resources.openRawResource(R.raw.book_328478)
    val bookContent = inputStream.bufferedReader().use { it.readText()}
    val book = Book.instance(bookContent)
    Log.d("reader", "${book.bookInfo.bookId}")
    elements.forEach { element ->
        when (element) {
            is ParsedElement.Text -> {
                //Log.d("bookparse", "displaying text, content: ${element.content}        ")
                spannableStringBuilder.append(element.content)
            }

            is ParsedElement.Font -> {
                val start = spannableStringBuilder.length
                spannableStringBuilder.append(element.content)
                val end = spannableStringBuilder.length
                applyFontCustomizations(spannableStringBuilder, element.fontTag, start, end)
            }

            is ParsedElement.WebLink -> {
                val start = spannableStringBuilder.length
                spannableStringBuilder.append(element.address)
                val end = spannableStringBuilder.length
                applyLinkCustomizations(
                    context,
                    spannableStringBuilder,
                    LinkType.WEB,
                    element.address,
                    start,
                    end,
                    viewModel,
                    navigationCallback
                )
            }

            is ParsedElement.InternalLinkSource -> {
                val start = spannableStringBuilder.length
                spannableStringBuilder.append(element.content)
                val end = spannableStringBuilder.length
                applyLinkCustomizations(
                    context,
                    spannableStringBuilder,
                    element.linkType,
                    element.key,
                    start,
                    end,
                    viewModel,
                    navigationCallback
                )
            }

            is ParsedElement.InternalLinkTarget -> {

            }

            is ParsedElement.Image -> {

                val byteArray: ByteArray = book.images[0].decodedData!!
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.test)
                val displayMetrics = context.resources.displayMetrics
                val screenWidth = displayMetrics.widthPixels
                val originalWidth = bitmap.width
                val originalHeight = bitmap.height
                val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()
                val newHeight = (200 * displayMetrics.density).toInt() // Convert 200dp to pixels
                val newWidth = (newHeight * aspectRatio).toInt()
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                val imageSpan = ImageSpan(context, resizedBitmap)
                spannableStringBuilder.append(" \n ") // Add a space before the image
                spannableStringBuilder.setSpan(
                    imageSpan,
                    spannableStringBuilder.length - 1,
                    spannableStringBuilder.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableStringBuilder.append(" \n ")
            }

            else -> {}
        }
    }

    textView.text = spannableStringBuilder
}


fun displayImage(imageView: ImageView, url: String) {
    // Implement logic to display image, e.g., using Glide or Picasso
    Glide.with(imageView.context).load(url).into(imageView)
}

fun loadBookTextFromRaw(context: Context): String {
    val inputStream = context.resources.openRawResource(R.raw.demo4)
    return inputStream.bufferedReader().use { it.readText() }
}



