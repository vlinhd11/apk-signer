/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

import java.security.Key

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * The <b>simple-and-weak</b> encryption utilities.
 *
 * @author Hai Bison.
 *
 */
object SimpleWeakEncryption {

    private lazy final val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private lazy final val SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1"
    private lazy final val SECRET_KEY_SPEC_ALGORITHM = "AES"

    /**
     * Only {@code 128} bits. If this is {@code 256}, some JVMs must need extra
     * tools to be installed. Poor Java :|
     */
    private lazy final val KEY_LEN = 128
    private lazy final val ITERATION_COUNT = Math.pow(2, 16).asInstanceOf[Int]
    private lazy final val SEPARATOR = "\t"
    private lazy final val DEFAULT_BASE64_FLAGS = Base64.NO_WRAP

    lazy final val UTF8 = "UTF-8"

    /**
     * Encrypts {@code data} by {@code key}.
     *
     * @param password
     *            the secret key.
     * @param data
     *            the data.
     * @return the encrypted data.
     */
    def encrypt(password: Array[Char], data: String): String = {
        var cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, genKey(password))

        Base64.encodeToString(cipher.getIV(), DEFAULT_BASE64_FLAGS) +
            SEPARATOR +
            Base64.encodeToString(
                cipher.doFinal(data.getBytes(UTF8)), DEFAULT_BASE64_FLAGS)
    } // encrypt()

    /**
     * Decrypts an encrypted string ({@code data}) by {@code key}.
     *
     * @param password
     *            the password.
     * @param data
     *            the data.
     * @return the decrypted string.
     */
    def decrypt(password: Array[Char], data: String): String = {
        val iSeparator = data.indexOf(SEPARATOR)
        var cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, genKey(password),
            new IvParameterSpec(
                Base64.decode(data.substring(0, iSeparator), DEFAULT_BASE64_FLAGS)))
        new String(
            cipher.doFinal(Base64.decode(
                data.substring(iSeparator + 1), DEFAULT_BASE64_FLAGS)),
            UTF8)
    } // decrypt()

    /**
     * Generates secret key.
     *
     * @param password
     *            the password.
     * @return the secret key.
     */
    private def genKey(password: Array[Char]): Key = {
        var factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM)
        var spec = new PBEKeySpec(
            password, new String(password).getBytes(UTF8), ITERATION_COUNT,
            KEY_LEN)
        new SecretKeySpec(
            factory.generateSecret(spec).getEncoded(),
            SECRET_KEY_SPEC_ALGORITHM)
    } // genKey()

}
