import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import org.json.JSONObject

data class FontStyle(
    val bold: Boolean,
    val italic: Boolean,
    val size: Float,
    val fontColor: String,
    val align: String,
    val underline: Boolean
)

fun getFontStyle(fontTag: String): FontStyle {
    val fontsJson = """
    {
        "T": {"bold": "1", "italic": "0", "size": "2", "font_color": "#ffFF0000", "backgroundColor": "0", "align": "c", "under_line": "1"},
        "t": {"bold": "1", "italic": "1", "size": "1.5", "font_color": "#0000FF", "background_color": "0", "align": "c"},
        "s": {"bold": "1", "italic": "0", "size": "1", "font_color": "#ff000099", "backgroundColor": "0", "align": "l", "under_line": "1"},
        "b": {"bold": "1", "size": "1.1"},
        "r": {"font_color": "#FF0000"},
        "A": {"bold": "1", "italic": "0", "size": "1.5", "font_color": "#ffFFff00", "align": "c", "under_line": "1"},
        "a": {"bold": "1", "italic": "0", "size": "1.5", "font_color": "#ff00ffff", "align": "c", "under_line": "0"},
        "b": {"bold": "1", "italic": "0", "size": "1.5", "font_color": "#ff00ff00", "align": "0", "under_line": "0"}
    }
    """

    val jsonObject = JSONObject(fontsJson)
    val fontObject = jsonObject.getJSONObject(fontTag)

    val fontStyle = FontStyle(
        bold = fontObject.optString("bold", "0") == "1",
        italic = fontObject.optString("italic", "0") == "1",
        size = fontObject.optString("size", "1").toFloat(),
        fontColor = fontObject.optString("font_color", "#000000"),
        align = fontObject.optString("align", "l"),
        underline = fontObject.optString("under_line", "0") == "1"
    )

    // Log the retrieved font style
    Log.d("FontStyle", "Tag: $fontTag, Style: $fontStyle")

    return fontStyle
}

fun applyFontCustomizations(spannable: SpannableStringBuilder, fontTag: String, start: Int, end: Int) {
    val fontStyle = getFontStyle(fontTag)

    if (fontStyle.bold) {
        spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    if (fontStyle.italic) {
        spannable.setSpan(StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    spannable.setSpan(RelativeSizeSpan(fontStyle.size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannable.setSpan(ForegroundColorSpan(Color.parseColor(fontStyle.fontColor)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (fontStyle.underline) {
        spannable.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    Log.d("ApplyFont", "Applied font style: $fontStyle from $start to $end")
}