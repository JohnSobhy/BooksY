sealed class ParsedElement {
    data class Text(val content: String) : ParsedElement()
    data class Font(val content: String, val fontTag: String) : ParsedElement()
    data class WebLink(val address: String) : ParsedElement()
    data class InternalLinkSource(var content: String, val linkType: LinkType, var key: String) : ParsedElement()
    data class InternalLinkTarget(var destination: Int, var key: String) : ParsedElement()
    data class Image(val url: String) : ParsedElement()
}