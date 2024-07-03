package reader.data

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
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

    ///////////////////////////////////////////////////////////////////////////////////////////////
    fun encodedBookContent(): String {
        val gson = Gson()
        val infoContent = gson.toJson(bookInfo)
        val encoding = gson.toJson(encoding)
        val bodyContent = body.string
        val newBody = bodyWithImages(bodyContent)
        // must be in coroutines
        return /*Encryption.encrypt(*/"$infoContent$SPLITTER$encoding$SPLITTER$newBody"/*)*/
    }

    private fun bodyWithImages(body: String): String {
        var newBody = ""
        val imageTagIndexes = getImageTagIndexes(body)
        var idx = 0
        for (i in body.indices) {
            if (idx < imageTagIndexes.size && i == imageTagIndexes[idx].endIdx) {
                val imageData = getImage(imageTagIndexes[idx].imageName)
                newBody += (IMAGE_SPLITTER + imageData)
                idx++
            }
            newBody += body[i]
        }
        return newBody
    }

    private fun getImage(imageName: String): String? {
        for (i in images)
            if (i.name == imageName) return i.encodedData
        return null
    }
    /////////////////////////////////////////////////////////////////////////////////////////////

    fun bookName() = "book_${bookInfo.bookId}.in"

    companion object {
        private const val SPLITTER = "!fdg@f#$%sff^ad&*(ag)s"

        ////////////////////////////////////////////////////////////////////
        private const val IMAGE_SPLITTER = "!f#$%sff^ad&*(ag)s"
        ////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////////////////////
        fun instance(encodedBookContent: String): Book {
            val gson = Gson()

            val bookContent = encodedBookContent
            val contents = bookContent.split(SPLITTER)
            val bookInfo = gson.fromJson(contents[0], BookInfo::class.java)
            val encoding = gson.fromJson(contents[1], Encoding::class.java)
            var bookBody = contents[2]

            val splitter = splitBodyToImagesAndBody(bookBody)
            bookBody = splitter.first
            val images = splitter.second

            println(bookBody)

            return Book(Body(bookBody), bookInfo, encoding, images)
        }


        private fun splitBodyToImagesAndBody(bodyContent: String): Pair<String, List<Image>> {
            val localFile = LocalFile.instance()
            val imageTagIndexes = getImageTagIndexes(bodyContent)
            //////////////////  here, we have content of image tag (image name and image date) //////////////
            /////////////////   so we split them in store them in list of images ////////////////////////////
            val images = mutableListOf<Image>()
            for (i in imageTagIndexes) {
                val image = bodyContent.substring(i.startIdx, i.endIdx)
                val contents = image.split(IMAGE_SPLITTER)
                val imageName = contents[0]
                val imageData = contents[1]
                images.add(Image(imageName, null, localFile.decode(imageData)))
            }

            /////////////////////   here, we have body content which contains image name and image data in image tag ////////////////////////
            ////////////////////    so we will remove all content in image tag and add image name only /////////////////////
            var idxOfBody = 0
            var idxOfImageTag = 0
            var newBody = ""
            var imageTag = imageTagIndexes[idxOfImageTag]     // initial value
            while (true) {
                if (idxOfBody == imageTag.startIdx) {
                    newBody += images[idxOfImageTag].name
                    idxOfBody = imageTag.endIdx      // this line to skip image name and image splitter and image data
                    if (idxOfImageTag < imageTagIndexes.size - 1) {
                        imageTag = imageTagIndexes[++idxOfImageTag]
                    }
                }
                newBody += bodyContent[idxOfBody]
                idxOfBody++
                if (idxOfBody == bodyContent.length) break
            }
            return Pair(newBody, images)
        }

        private fun getImageTagIndexes(body: String): List<ImageTag> {
            val regex = Regex("##I(.*?):", RegexOption.DOT_MATCHES_ALL)
            val indexes = mutableListOf<ImageTag>() // pair of image name and last idx of name
            regex.findAll(body).forEach {
                val imageName = it.groupValues[1]
                indexes.add(ImageTag(imageName, it.range.first, it.range.last))
            }
            return indexes
        }

/////////////////////////////////////////////////////////////////////////////////////////////////////


        fun instance(path: String, folderInfo: FolderInfo): Book {
            val mainPath = Path.getMainPath(path)
            val bookBody = getBookBody(mainPath, folderInfo)
            val bookInfo = getBookInfo(mainPath, folderInfo)
            val encoding = getEncoding(mainPath, folderInfo)
            return Book(bookBody, bookInfo, encoding, listOf())
        }


        private fun getBookBody(mainPath: String, folderInfo: FolderInfo): Body {
            val localFile = LocalFile.instance()
            println("$mainPath${folderInfo.bookFile}")
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
            val tag = getTags(mainPath, folderInfo)
            val fonts = getFonts(mainPath, folderInfo)
            return Encoding(tag, fonts)
        }

        private fun getTags(mainPath: String, folderInfo: FolderInfo): Tag {
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

data class ImageTag(val imageName: String, val startIdx: Int, val endIdx: Int)