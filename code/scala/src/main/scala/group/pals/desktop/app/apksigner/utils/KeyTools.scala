/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.X509Certificate

import group.pals.desktop.app.apksigner.i18n.Messages
import group.pals.desktop.app.apksigner.i18n.R

/**
 * Utilities for keystore files.
 *
 * @author Hai Bison
 */
object KeyTools {

    /**
     * "JKS"
     */
    lazy final val KEYSTORE_TYPE_JKS = "JKS"

    /**
     * "JCEKS"
     */
    lazy final val KEYSTORE_TYPE_JCEKS = "JCEKS"

    /**
     * "PKCS12"
     */
    lazy final val KEYSTORE_TYPE_PKCS12 = "PKCS12"

    /**
     * Default keystore type.
     */
    lazy final val DEFAULT_KEYSTORE_TYPE = KEYSTORE_TYPE_JKS

    /**
     * Lists entries in a keystore file.
     *
     * @param jdkPath
     *            the JDK path, can be {@code null} on Unix system.
     * @param keyFile
     *            the keystore file.
     * @param storepass
     *            the keystore password.
     * @return the information, never be {@code null}.
     * @deprecated Use {@link #listEntries(File, String, char[])} instead.
     */
    @deprecated
    def listEntries(jdkPath: File, keyFile: File,
                    storepass: Array[Char]): CharSequence = {
        /*
         * JDK for Linux does not need to specify full path.
         */
        var keytool =
            if (jdkPath != null && jdkPath.isDirectory())
                jdkPath.getAbsolutePath() + "/keytool.exe"
            else "keytool"

        val console = new StringBuilder()

        /*
         * keytool -list -v -keystore aaa.keystore -storepass XXX
         */
        var pb = new ProcessBuilder(
            keytool, "-list",
            "-v", "-keystore", keyFile.getAbsolutePath(), "-storepass",
            new String(storepass))
        try {
            var p = pb.start()

            var stream = p.getInputStream()
            try {
                var read = 0
                var buf = new Array[Byte](Files.FILE_BUFFER)
                while ({ read = stream.read(buf); read } > 0) {
                    console.append(new String(buf, 0, read))
                }
            } finally {
                if (stream != null)
                    stream.close()
            }

            /*
             * TODO: parse output for errors, warnings...
             */

            p.waitFor()
        } catch {
            case t: Throwable =>
                console.append("*** ERROR ***\n\n").append(t)
        }

        console
    } // listEntries()

    /**
     * Lists entries in a keystore file.
     *
     * @param keyFile
     *            the keystore file.
     * @param keystoreType
     *            the keystore type.
     * @param storepass
     *            the keystore password.
     * @return the information, never be {@code null}.
     */
    def listEntries(keyFile: File, keystoreType: String,
                    storepass: Array[Char]): CharSequence = {
        val result = new StringBuilder()

        try {
            val inputStream = new BufferedInputStream(
                new FileInputStream(keyFile), Files.FILE_BUFFER)
            try {
                var keyStore = KeyStore.getInstance(keystoreType)
                keyStore.load(inputStream, storepass)

                /*
                 * HEADER
                 */

                result.append("%s: %s\n".format(
                    Messages.getString(R.string.keystore_type),
                    keyStore.getType()))
                result.append("%s: %s\n".format(
                    Messages.getString(R.string.keystore_provider),
                    keyStore.getProvider()))
                result.append("\n")

                val entryCount = keyStore.size()
                if (entryCount <= 1)
                    result.append(Messages.getString(
                        R.string.pmsg_your_keystore_contains_x_entry,
                        entryCount))
                else
                    result.append(Messages.getString(
                        R.string.pmsg_your_keystore_contains_x_entries,
                        entryCount))
                result.append("\n\n")

                /*
                 * ENTRIES
                 */

                var aliases = keyStore.aliases()
                while (aliases.hasMoreElements()) {
                    val alias = aliases.nextElement()
                    val cert = keyStore.getCertificate(alias)

                    result.append("%s: %s\n".format(
                        Messages.getString(R.string.alias_name), alias))
                    result.append("%s: %s\n".format(
                        Messages.getString(R.string.creation_date),
                        keyStore.getCreationDate(alias)))
                    result.append("%s: %s\n".format(
                        Messages.getString(R.string.entry_type),
                        cert.getType()))

                    val certChain = keyStore.getCertificateChain(alias)
                    if (certChain != null) {
                        result.append("%s: %,d\n".format(Messages
                            .getString(R.string.certificate_chain_length),
                            certChain.length))
                        for (i <- 0 until certChain.length) {
                            result.append("\t%s[%,d]:\n".format(
                                Messages.getString(R.string.certificate),
                                i + 1))

                            if (certChain(i).isInstanceOf[X509Certificate]) {
                                var x509Cert = certChain(i).asInstanceOf[X509Certificate]

                                result.append("\t\t%s: %s\n".format(
                                    Messages.getString(R.string.owner),
                                    x509Cert.getIssuerX500Principal()
                                        .getName()))
                                result.append("\t\t%s: %s\n".format(
                                    Messages.getString(R.string.issuer),
                                    x509Cert.getIssuerX500Principal()
                                        .getName()))
                                result.append(
                                    "\t\t%s: %x\n".format(
                                        Messages.getString(R.string.serial_number),
                                        x509Cert.getSerialNumber()))
                                result.append("\t\t")
                                    .append(Messages.getString(
                                        R.string.pmsg_valid_from_until,
                                        x509Cert.getNotBefore(),
                                        x509Cert.getNotAfter()))
                                    .append('\n')
                            } // if

                            result.append(
                                "\t\t%s:\n".format(
                                    Messages.getString(R.string.certificate_fingerprints)))
                            for (
                                algorithm <- Array(Hasher.MD5, Hasher.SHA1,
                                    Hasher.SHA256)
                            ) {
                                var hash = Hasher
                                    .calcHash(algorithm,
                                        certChain(i).getEncoded(), true)
                                    .toString().toUpperCase()
                                result.append("\t\t\t%s: %s\n".format(
                                    algorithm, hash))
                            }
                        }
                    }
                } // while
            } finally {
                inputStream.close()
            }
        } catch {
            case e: Exception => result.append(L.printStackTrace(e))
        }

        result
    } // listEntries()

    /**
     * Gets all alias names from {@code keyFile}.
     *
     * @param keyFile
     *            the keyfile.
     * @param keystoreType
     *            the keystore type.
     * @param storepass
     *            the password.
     * @return list of alias names, can be empty.
     */
    def getAliases(keyFile: File, keystoreType: String,
                   storepass: Array[Char]): List[String] = {
        var result = List[String]()

        try {
            val inputStream = new BufferedInputStream(
                new FileInputStream(keyFile), Files.FILE_BUFFER)
            try {
                var keyStore = KeyStore.getInstance(keystoreType)
                keyStore.load(inputStream, storepass)

                var aliases = keyStore.aliases()
                while (aliases.hasMoreElements())
                    result :+= aliases.nextElement()
            } finally {
                inputStream.close()
            }
        } catch {
            /*
             * Ignore it.
             */
            case _: Throwable =>
        }

        result
    } // getAliases()

}
