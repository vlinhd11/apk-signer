/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

import java.io.File

/**
 * System utilities.
 *
 * @author Hai Bison
 * @since v1.9 beta
 */
object Sys {

    /**
     * Debug flag.
     */
    lazy final val DEBUG = false

    /**
     * The app name.
     */
    lazy final val APP_NAME = "apk-signer"

    /**
     * The app version code.
     */
    lazy final val APP_VERSION_CODE = 43

    /**
     * The app version name.
     */
    lazy final val APP_VERSION_NAME = "1.9 beta"

    /**
     * Gets app .jar file.
     */
    def appJar(): File = {
        new File(Sys.getClass().getProtectionDomain().getCodeSource()
            .getLocation().toString())
    } // appJar()

    /**
     * Gets app root directory.
     */
    def appDir(): File = appJar().getParentFile()

}// Sys