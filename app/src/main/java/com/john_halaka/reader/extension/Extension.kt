package reader.extension

fun String?.indexesOf(substring: String): List<Int> {
    val list = mutableListOf<Int>()
    this?.let {
        for (i in this.indices) {
            if (this[i] == substring[0]) {
                var isImageTag = true
                for (j in substring.indices) {
                    if (this[i + j] != substring[j]) {
                        isImageTag = false
                        break
                    }
                }
                if (isImageTag) {
                    list.add(i + substring.length)
                }
            }
        }
    }
    return list
}