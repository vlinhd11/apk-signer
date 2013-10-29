/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Helper class to generate keystore file.
 *
 * @author Hai Bison
 */
object KeyGen {

    /**
     * Generates new keystore file.
     *
     * @param jdkPath
     *            the JDK path, can be {@code null} on Unix system.
     * @param target
     *            the target file which will be generated to.
     * @param storepass
     *            the keystore's password.
     * @param alias
     *            the keystore's alias name.
     * @param keypass
     *            the keystore's alias password.
     * @param aliasYears
     *            the validity, in years.
     * @param coName
     *            the company name.
     * @param ouName
     *            the organization unit name.
     * @param oName
     *            the organization name.
     * @param city
     *            the city name.
     * @param state
     *            the state name.
     * @param country
     *            the country ISO code.
     * @throws IOException
     *             if any occurred.
     * @throws InterruptedException
     *             if any occurred.
     */
    def genKey(jdkPath: File, target: File, storepass: Array[Char],
               alias: String, keypass: Array[Char], aliasYears: Int, coName: String,
               ouName: String, oName: String, city: String, state: String,
               country: String) {
        target.delete()

        /*
         * keytool -genkey -sigalg MD5withRSA -digestalg SHA1 -alias ALIAS_NAME
         * -keypass KEY_PASS -validity YEARS -keystore TARGET_FILE -storepass
         * STORE_PASS -genkeypair -dname
         * "CN=Mark Jones, OU=JavaSoft, O=Sun, L=city, S=state C=US"
         */

        var values = Array(coName, ouName, oName, city, state, country)
        var keys = Array("CN", "OU", "O", "L", "S", "C")
        var dname = ""
        for (i <- 0 until values.length) {
            if (!Texts.isEmpty(values(i)))
                dname += "%s=%s ".format(keys(i), values(i))
        }
        dname = dname.trim()

        /*
         * JDK for Linux does not need to specify full path
         */
        var keytool =
            if (jdkPath != null && jdkPath.isDirectory())
                jdkPath.getAbsolutePath() + File.separator + "keytool.exe"
            else "keytool"

        var pb = new ProcessBuilder(
            keytool,
            "-genkey", "-keyalg", "RSA", "-alias", alias, "-keypass",
            new String(keypass), "-validity", aliasYears.toString(),
            "-keystore", target.getAbsolutePath(), "-storepass",
            new String(storepass), "-genkeypair", "-dname",
            "%s".format(dname))
        var p = pb.start()

        var sb = new StringBuffer()
        var stream = p.getInputStream()
        try {
            var read = 0
            var buf = new Array[Byte](Files.FILE_BUFFER)
            while ({ read = stream.read(buf); read } > 0)
                sb.append(new String(buf, 0, read))
        } finally {
            if (stream != null)
                stream.close()
        }

        /*
         * TODO: parse output for errors, warnings...
         */

        p.waitFor()

        if (!target.isFile())
            throw new IOException("Error: " + sb)
    } // genKey()

}