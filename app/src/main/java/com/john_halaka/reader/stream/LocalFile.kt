package reader.stream

import java.io.File
import java.util.*

class LocalFile private constructor() {
    fun read(path: String): String {
        val file = File(path)
        return file.readText()
    }

    fun write(path: String, content: String) {
        val file = File(path)
        file.writeText(content)
    }

    fun encode(data: ByteArray): String {
        return Base64.getEncoder().encodeToString(data)
    }

    fun decode(data: String): ByteArray {
        return Base64.getDecoder().decode(data)
    }

    companion object {
        private var obj: LocalFile? = null
        fun instance(): LocalFile {
            if (obj == null) obj = LocalFile()
            return obj!!
        }
    }
}