package reader.data

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.john_halaka.reader.data.Body
import com.john_halaka.reader.data.BookInfo
import reader.encryption.Encryption
import reader.extension.indexesOf
import reader.stream.LocalFile
import java.io.File

class Book(
    val body: Body,
    val bookInfo: BookInfo,
    val encoding: Encoding,
    var images: List<Image> // this variable is var to add images later
) {

    fun isAllImagesExist(mainPath: String): String? {
        val allFilesInMainPath = getAllFilesInFolder(mainPath)
        var message = ""
        allFilesInMainPath?.let {
            this.images = getImagesName()
            for (i in images) {
                var isImageExist = false
                for (j in allFilesInMainPath) {
                    if (i.name == j.name) {
                        isImageExist = true
                        break
                    }
                }
                if (!isImageExist) {
                    message += "${i.name} does not exist in book folder \n"
                }
            }
        }
        return if (message == "") {
            getImages(mainPath)
            null
        } else message
    }

    private fun getAllFilesInFolder(folderPath: String): List<File>? {
        val bookFolder = File(folderPath)
        val files = bookFolder.listFiles()?.toList()
        return files
    }

    private fun getImagesName(): List<Image> {
        val indexes = body.string.indexesOf("${encoding.tag.tagStart}${encoding.tag.image}")
        val images = mutableListOf<Image>()
        for (i in indexes) {
            images.add(Image(getImageName(i), null))
        }
        return images
    }

    private fun getImageName(startIdx: Int): String {
        var imageName = ""
        for (i in startIdx..<body.string.length) {
            if (body.string[i] != ':')
                imageName += body.string[i]
            else break
        }
        return imageName
    }

    private fun getImages(mainPath: String) {
        for (i in images.indices) {
            images[i].getMyData(mainPath)
        }
    }

    fun encodedBookContent(): String {
        val gson = Gson()
        val bodyContent = body.string
        val infoContent = gson.toJson(bookInfo)
        val encoding = gson.toJson(encoding)
        var images = ""

        for (i in this.images) {
            images += "${i.name}$SPLITTER${i.encodedData}$SPLITTER"
        }
        // must be in coroutines
        return Encryption.encrypt("$infoContent$SPLITTER$encoding$SPLITTER$bodyContent$SPLITTER$images")
    }

    fun bookName() = "Book_${bookInfo.bookId}.in"

    companion object {
        private const val SPLITTER = "!fdg@f#$%sff^ad&*(ag)s"

        fun instance(encodedBookContent: String): Book {
            val gson = Gson()

            val bookContent = Encryption.decrypt(encodedBookContent)
            val contents = bookContent.split(SPLITTER)
            val bookInfo = gson.fromJson(contents[0], BookInfo::class.java)
            val encoding = gson.fromJson(contents[1], Encoding::class.java)
            val bookBody = contents[2]


            return Book(Body(bookBody), bookInfo, encoding, getImages(contents))
        }

        fun instance(path: String, folderInfo: FolderInfo): Book {
            val mainPath = Path.getMainPath(path)
            val bookBody = getBookBody(mainPath, folderInfo)
            val bookInfo = getBookInfo(mainPath, folderInfo)
            val encoding = getEncoding(mainPath, folderInfo)
            return Book(bookBody, bookInfo, encoding, listOf())
        }


        private fun getImages(contents: List<String>): List<Image> {
            val localFile = LocalFile.instance()

            val images = mutableListOf<Image>()

            var idx = 3
            while (idx < contents.size - 1) {
                val imageName = contents[idx]
                val decodedImage = localFile.decode(contents[idx + 1])
                images.add(Image(imageName, null, decodedImage))
                idx += 2
            }
            return images
        }

        private fun getBookBody(mainPath: String, folderInfo: FolderInfo): Body {
            val localFile = LocalFile.instance()
            val bookContent = localFile.read("$mainPath${folderInfo.bookFile}")
            return Body(bookContent)
        }

        private fun getBookInfo(mainPath: String, folderInfo: FolderInfo): BookInfo {
            val localFile = LocalFile.instance()
            val gson = Gson()
            val bookInfoContent = localFile.read("$mainPath${folderInfo.bookInfoFile}")
            val bookInfo = gson.fromJson(bookInfoContent, BookInfo::class.java)
            return bookInfo
        }

        private fun getEncoding(mainPath: String, folderInfo: FolderInfo): Encoding {
            val tag = getTag(mainPath, folderInfo)
            val fonts = getFonts(mainPath, folderInfo)
            return Encoding(tag, fonts)
        }

        private fun getTag(mainPath: String, folderInfo: FolderInfo): Tag {
            val localFile = LocalFile.instance()
            val gson = Gson()
            val encodingContent = localFile.read("$mainPath${folderInfo.encodingFile}")
            val jsonObject = JsonParser.parseString(encodingContent).asJsonObject
            val tagJsonObj = jsonObject.getAsJsonObject("tag")
            val tag = gson.fromJson(tagJsonObj, Tag::class.java)
            return tag
        }

        private fun getFonts(mainPath: String, folderInfo: FolderInfo): Map<String, Font> {
            val localFile = LocalFile.instance()

            val encodingContent = localFile.read("$mainPath${folderInfo.encodingFile}")
            val jsonObject = JsonParser.parseString(encodingContent).asJsonObject
            val fontsJsonObj = jsonObject.getAsJsonObject("fonts")
            val mapType = object : TypeToken<Map<String, Font>>() {}.type
            val gson = Gson()
            val fonts: Map<String, Font> = gson.fromJson(fontsJsonObj, mapType)
            return fonts
        }
    }

}