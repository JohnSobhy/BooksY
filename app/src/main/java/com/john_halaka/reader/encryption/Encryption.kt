package reader.encryption

class Encryption {

    companion object {

        // must be in coroutines
        fun encrypt(data: String): String {
            var cipher = ""
            val key = 15
            for (i in data) {
                cipher += (i + key)
            }
            return cipher
        }

        fun decrypt(cipher: String): String {
            var plain = ""
            val key = 15
            for (i in cipher) {
                plain += (i - key)
            }
            return plain
        }
    }
}