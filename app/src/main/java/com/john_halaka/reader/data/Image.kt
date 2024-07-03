package reader.data

import reader.stream.LocalFile
import java.io.File

class Image(val name: String, var encodedData: String?, var decodedData: ByteArray? = null) {
    fun getMyData(mainPath: String) {
        val imagePath = "$mainPath$name"
        val imageFile = File(imagePath)
        val imageBytes = imageFile.readBytes()
        val localFile = LocalFile.instance()
        encodedData = localFile.encode(imageBytes)
    }
}