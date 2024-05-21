package encryption

import java.security.KeyPairGenerator
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESProcessor {

    private val numbers = listOf(-103, 24, 6, 16, -112, 73, 87, 20, 116, 56, 86, -49, 20, -82, -23, 102, 126, -26, -24, 38, -26, -12, -64, -29, 8, 115, -99, 19, 76, -48, -119, -91)

    private val SECRET_KEY = numbers.map { it.toByte() }.toByteArray()

    fun generateAESKey(keySize: Int = 256): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(keySize)
        val secretKey = keyGenerator.generateKey()
        println(secretKey.encoded.joinToString(", ") { it.toString() })
        return keyGenerator.generateKey()
    }

    fun aesEncrypt(data: ByteArray): String {
        val secretKey = SecretKeySpec(SECRET_KEY, "AES");
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(ByteArray(16)) // Use a secure IV in production
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        val encryptedDataByteArray =  cipher.doFinal(data)
        return Base64.getEncoder().encodeToString(encryptedDataByteArray)
    }

    fun aesDecrypt(encryptedData: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(SECRET_KEY, "AES");
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(ByteArray(16)) // Use the same IV as used in encryption
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(encryptedData)
    }
}