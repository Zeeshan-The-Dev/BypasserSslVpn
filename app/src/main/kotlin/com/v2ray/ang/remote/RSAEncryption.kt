package com.v2ray.ang.remote

import android.content.Context
import android.util.Base64
import android.util.Log
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.StringReader
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

/**
 * @author Muhammad Shahab
 * @date 9/6/2019
 * @usage Class performs RSA encryption using pem file
 */
class RSAEncryption(private val pemFileName: String, private val ctx: Context) {
    @get:Throws(Exception::class)
    private val publicKey: PrivateKey?
        /**
         * @usage Extract key from pem file and make RSA public key
         * @return PublicKey
         */
        private get() {
            val publicKeyString = getKeyFromFile(ctx)
            return stringToPrivate(publicKeyString)
        }

    /**
     * @usage It encrypts the text using public key provided by pem file
     * @return String
     */
    fun encryptData(text: String?): String {
        var encoded = ""
        var encrypted: ByteArray? = null
        try {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding") //or try with "RSA"
            cipher.init(Cipher.DECRYPT_MODE, publicKey)
            encrypted = cipher.doFinal(Base64.decode(text, Base64.DEFAULT))
            encoded = String(encrypted)
            /*  encoded = Base64.encodeToString(encrypted, Base64.DEFAULT);
            encoded = encoded.replaceAll("(\\r|\\n)", "");*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("enccriptData", encoded)
        return encoded
    }

    /**
     * @param publicKeyString
     * @return PublicKey
     */
    @Throws(Exception::class)
    private fun generatePublicKey(publicKeyString: String): PrivateKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKeyBytes =
            Base64.decode(publicKeyString.toByteArray(charset("UTF-8")), Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(publicKeyBytes)
        // X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePrivate(keySpec)
    }

    /**
     * Load the key/PEM from a file and strip the header/footer.
     * @param ctx
     * @return String
     */
    @Throws(Exception::class)
    private fun getKeyFromFile(ctx: Context): String {
        val input = ctx.assets.open(pemFileName)
        val buffer = ByteArray(bufferSize_)
        var len = 0
        val keyBuffer = ByteArrayOutputStream()
        while (input.read(buffer).also { len = it } != -1) {
            keyBuffer.write(buffer, 0, len)
        }
        var str = String(keyBuffer.toByteArray())
        Log.d("Public-key", str)
        str = str.replace(headerTag, "")
        str = str.replace(footerTag, "")
        str = str.replace("(\\r|\\n)".toRegex(), "")
        return str
    }

    companion object {
        private const val bufferSize_ = 32
        private const val headerTag = "-----BEGIN PRIVATE KEY-----"
        private const val footerTag = "-----END PRIVATE KEY-----"
        @Throws(
            NoSuchAlgorithmException::class,
            IllegalBlockSizeException::class,
            BadPaddingException::class
        )
        fun stringToPrivate(private_key: String?): PrivateKey? {
            try {
                // Read in the key into a String
                val pkcs8Lines = StringBuilder()
                val rdr =
                    BufferedReader(StringReader(private_key))
                var line: String?
                while (rdr.readLine().also { line = it } != null) {
                    pkcs8Lines.append(line)
                }

                // Remove the "BEGIN" and "END" lines, as well as any whitespace
                var pkcs8Pem = pkcs8Lines.toString()
                pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "")
                pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "")
                pkcs8Pem = pkcs8Pem.replace("\\s+".toRegex(), "")

                // Base64 decode the result
                val pkcs8EncodedBytes =
                    Base64.decode(pkcs8Pem, Base64.DEFAULT)

                // extract the private key
                val keySpec =
                    PKCS8EncodedKeySpec(pkcs8EncodedBytes)
                val kf = KeyFactory.getInstance("RSA")
                return kf.generatePrivate(keySpec)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                return null
            } catch (e: InvalidKeySpecException) {
                e.printStackTrace()
                return null
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }


    fun decryptWithPrivateKey(encryptedData: String): String {
        try {
            val privateKeyBytes = Base64.decode( getKeyFromFile(ctx), Base64.DEFAULT)
            val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            val privateKey = keyFactory.generatePrivate(privateKeySpec)

            val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)

            val encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            return String(decryptedBytes)
        } catch (e: Exception) {
            throw Exception("Failed to decrypt data", e)
        }
    }
}