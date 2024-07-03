package reader.validation

import reader.data.FolderInfo

class FolderInfoValidation {
    companion object {
        fun isValid(folderInfo: FolderInfo): Boolean {
            val isBookInfoFileValid = folderInfo.bookInfoFile.trim()
                .isNotEmpty() && folderInfo.bookInfoFile.contains(".json") && folderInfo.bookInfoFile.length > 5
            val isBookFileValid = folderInfo.bookFile.trim()
                .isNotEmpty() && folderInfo.bookFile.contains(".txt") && folderInfo.bookFile.length > 4
            val isEncodingFileValid = folderInfo.encodingFile.trim()
                .isNotEmpty() && folderInfo.encodingFile.contains(".json") && folderInfo.encodingFile.length > 5

            return isBookInfoFileValid && isBookFileValid && isEncodingFileValid
        }
    }
}