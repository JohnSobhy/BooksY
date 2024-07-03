package reader.data

class Path() {
    companion object {
        fun getMainPath(path: String): String {
            var lastIdx = 0
            for (i in path.length - 1 downTo 0) {
                if (path[i] == '\\') {
                    lastIdx = i
                    break
                }
            }
            var mainPath = ""
            for (i in 0..lastIdx) mainPath += path[i]
            return mainPath
        }
    }
}