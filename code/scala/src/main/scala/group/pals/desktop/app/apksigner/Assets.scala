/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner

import java.awt.Font
import java.awt.FontFormatException
import java.awt.Image
import java.awt.Toolkit
import java.io.IOException

import group.pals.desktop.app.apksigner.utils.Files

/**
 * The assets.
 *
 * @author Hai Bison
 *
 */
object Assets {

    private var mDefaultFont, mDefaultMonoFont: Font = null
    private var mIconLogo, mIconSplash: Image = null

    /**
     * Gets font from resource.
     *
     * @param resName
     *            the resource name to the font.
     * @return the font, or {@code null} if any error occurred.
     */
    def getFont(resName: String): Font = {
        try {
            var inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(resName)
            try {
                return Font.createFont(Font.TRUETYPE_FONT, inputStream)
            } finally {
                if (inputStream != null)
                    inputStream.close()
            }
        } catch {
            case e: FontFormatException =>
                // TODO Auto-generated catch block
                e.printStackTrace()
            case e: IOException =>
                // TODO Auto-generated catch block
                e.printStackTrace()
        }

        null
    } // getFont()

    /**
     * Gets default font.
     *
     * @return the default font.
     */
    def defaultFont(): Font = {
        if (mDefaultFont == null)
            mDefaultFont = getFont("assets/fonts/Roboto-Regular.ttf")
                .deriveFont(12f)
        mDefaultFont
    } // defaultFont()

    /**
     * Gets default mono font.
     *
     * @return the default mono font.
     */
    def defaultMonoFont(): Font = {
        if (mDefaultMonoFont == null)
            mDefaultMonoFont = getFont("assets/fonts/SourceCodePro-Regular.ttf")
                .deriveFont(12.5f)
        mDefaultMonoFont
    } // defaultMonoFont()

    /**
     * Gets the icon logo.
     *
     * @return the icon logo.
     */
    def iconLogo(): Image = {
        if (mIconLogo == null)
            mIconLogo = Toolkit.getDefaultToolkit().getImage(
                classOf[SplashDialog]
                    .getResource("/assets/images/logo_256.png"))
        mIconLogo
    } // iconLogo()

    /**
     * Gets the icon splash.
     *
     * @return the icon splash.
     */
    def iconSplash(): Image = {
        if (mIconSplash == null)
            mIconSplash = Toolkit.getDefaultToolkit().getImage(
                classOf[SplashDialog]
                    .getResource("/assets/images/logo_399x144.png"))
        mIconSplash
    } // iconSplash()

    /**
     * Get pattern HTML about.
     *
     * @return the pattern HTML about.
     */
    def pHtmlAbout(): CharSequence = {
        val result = new StringBuilder()
        var inputStream = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("assets/phtml_about")
        try {
            val buf = new Array[Byte](Files.FILE_BUFFER)
            var read = 0
            try {
                while ({ read = inputStream.read(buf); read } > 0)
                    result.append(new String(buf, 0, read))
            } catch {
                case e: IOException =>
                    e.printStackTrace()
            }
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close()
            } catch {
                case e: IOException =>
                    // TODO Auto-generated catch block
                    e.printStackTrace()
            }
        }

        result
    } // pHtmlAbout()

}
