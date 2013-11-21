/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Hashing tool.
 *
 * @author Hai Bison
 * @since v1.6.7 beta
 */
object Hasher {

    /**
     * "MD5"
     */
    lazy final val MD5 = "MD5"

    /**
     * "SHA-1"
     */
    lazy final val SHA1 = "SHA-1"

    /**
     * "SHA-256"
     */
    lazy final val SHA256 = "SHA-256"

    /**
     * Calculates hash string of {@code data}.
     *
     * @param algorithm
     *            the algorithm.
     * @param data
     *            the input data.
     * @param formatAsFingerprint
     *            {@code true} to format the result as a digital fingerprint.
     * @return the hash string, or an empty string if {@code algorithm} is not
     *         supported.
     */
    def calcHash(algorithm: String, data: Array[Byte],
                 formatAsFingerprint: Boolean): CharSequence = {
        try {
            val md = MessageDigest.getInstance(algorithm)
            md.update(data)

            val bi = new BigInteger(1, md.digest())
            val result = new StringBuilder(
                ("%0" + (md.digest().length * 2) + "x").format(bi))
            if (formatAsFingerprint) {
                val count = result.length() / 2 - 1
                for (i <- 1 to count)
                    result.insert(3 * i - 1, ':')
            }

            result
        } catch {
            case e: NoSuchAlgorithmException =>
                e.printStackTrace()
                ""
        }
    } // calcHash()

}
