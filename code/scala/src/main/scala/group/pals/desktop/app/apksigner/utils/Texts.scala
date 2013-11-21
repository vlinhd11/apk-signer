/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

import java.text.DateFormat
import java.util.Date

/**
 * Text utilities.
 *
 * @author Hai Bison
 * @since v1.6 beta
 */
object Texts {

    /**
     * "UTF-8"
     */
    lazy final val UTF8 = "UTF-8"

    /**
     * An empty string.
     */
    lazy final val EMPTY = ""

    /**
     * A {@code null} string.
     */
    lazy final val NULL = null

    /**
     * Regex to filter APK files.
     */
    lazy final val REGEX_APK_FILES = "(?si).+\\.apk"

    /**
     * Regex to filter keystore files.
     */
    lazy final val REGEX_KEYSTORE_FILES = "(?si).+\\.keystore"

    /**
     * Regex to filter JAR files.
     */
    lazy final val REGEX_JAR_FILES = "(?si).+\\.jar"

    /**
     * Regex to filter ZIP files.
     */
    lazy final val REGEX_ZIP_FILES = "(?si).+\\.zip"

    /**
     * File extension of APK files.
     */
    lazy final val FILE_EXT_APK = ".apk"

    /**
     * File extension of keystore files.
     */
    lazy final val FILE_EXT_KEYSTORE = ".keystore"

    /**
     * Converts {@code size} (in bytes) to string. This tip is from:
     * http://stackoverflow.com/a/5599842/942821
     *
     * @param size
     *            the size in bytes.
     * @return e.g.:<br>
     *         - 128 B<br>
     *         - 1.5 KB<br>
     *         - 10 MB<br>
     *         - ...
     */
    def sizeToStr(size: Double): String = {
        if (size <= 0) return "0 B"

        val units = Array("", "Ki", "Mi", "Gi", "Ti", "Pi", "Ei", "Zi", "Yi")
        val blockSize = 1024

        var digitGroups = (Math.log10(size) / Math.log10(blockSize)).asInstanceOf[Int]
        if (digitGroups >= units.length)
            digitGroups = units.length - 1
        var _size = size / Math.pow(blockSize, digitGroups)

        "%s %%sB".format(if (digitGroups == 0) "%,.0f" else "%,.2f").format(
            _size, units(digitGroups))
    } // sizeToStr()

    /**
     * Converts a percentage to string.
     *
     * @param percent
     * @return
     */
    def percentToStr(percent: Float): String = percentToStr(percent.asInstanceOf[Double])

    /**
     * Converts a percentage to string.
     *
     * @param percent
     * @return
     */
    def percentToStr(percent: Double): String = {
        if (percent == 0)
            "0%"
        else if (percent < 100)
            "%02.02f%%".format(percent)
        else
            "100%"
    } // percentToStr()

    /**
     * Checks whether {@code s} is empty or {@code null}.
     *
     * @param s
     *            the string to check.
     * @return {@code true} or {@code false}.
     */
    def isEmpty(s: CharSequence) = s == null || s.length() == 0

    /**
     * Formats {@code date} based on current locale.
     *
     * @param date
     *            the date.
     * @return the formatted string of {@code date}.
     */
    def formatDate(date: Date) = DateFormat.getInstance().format(date)

}
