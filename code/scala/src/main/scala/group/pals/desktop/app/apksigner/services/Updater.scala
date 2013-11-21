/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.services

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger
import java.net.HttpURLConnection
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Properties
import java.util.Timer
import java.util.TimerTask
import java.util.regex.Pattern

import BaseThread.MSG_DONE
import group.pals.desktop.app.apksigner.i18n.Messages
import group.pals.desktop.app.apksigner.i18n.R
import group.pals.desktop.app.apksigner.utils.Files
import group.pals.desktop.app.apksigner.utils.Hasher
import group.pals.desktop.app.apksigner.utils.L
import group.pals.desktop.app.apksigner.utils.Network
import group.pals.desktop.app.apksigner.utils.SpeedTracker
import group.pals.desktop.app.apksigner.utils.Sys
import group.pals.desktop.app.apksigner.utils.Texts

/**
 * Application updater service.
 *
 * @author Hai Bison
 * @since v1.6 beta
 */
class Updater extends BaseThread {

    import BaseThread._

    private lazy final val CLASSNAME = classOf[Updater].getName()

    /**
     * The URLs pointing to `update.properties` file. We should try them all (in
     * case one of them doesn't exist, or for some reason we can't reach it).
     */
    lazy final val URLS_UPDATE_PROPERTIES = Array(
        "http://dl.bintray.com/hai%20bison%20apps/android/apk-signer/update.properties",
        "https://bitbucket.org/haibisonapps/apk-signer/downloads/update.properties",
        "https://sites.google.com/site/haibisonapps/apps/apk-signer/update.properties")

    /**
     * The app version code.
     */
    lazy final val KEY_APP_VERSION_CODE = "app_version_code"

    /**
     * The app version name.
     */
    lazy final val KEY_APP_VERSION_NAME = "app_version_name"

    /**
     * The download URI.
     */
    lazy final val KEY_DOWNLOAD_URI = "download_uri"

    /**
     * The download filename.
     */
    lazy final val KEY_DOWNLOAD_FILENAME = "download_filename"

    /**
     * The SHA-1 of new file.
     */
    lazy final val KEY_SHA1 = "SHA-1"

    /**
     * Maximum filesize allowed for `update.properties`.
     */
    lazy final val MAX_UPDATE_PROPERTIES_FILESIZE = 9 * 1024

    /**
     * Maximum filesize allowed for the new version.
     */
    lazy final val MAX_UPDATE_FILESIZE =
        if (Sys.DEBUG) Int.MaxValue
        else 9 * 1024 * 1024

    /**
     * There is a local update file available.
     */
    lazy final val MSG_LOCAL_UPDATE_AVAILABLE = 1

    /**
     * The update filesize exceeds limit.
     */
    lazy final val MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT = 2

    /**
     * The update progress (percentage done...)
     */
    lazy final val MSG_UPDATE_PROGRESS = 3

    /**
     * The update cancelled.
     */
    lazy final val MSG_UPDATE_CANCELLED = 4

    /**
     * The update finished.
     */
    lazy final val MSG_UPDATE_FINISHED = 5

    /**
     * Used for debugging...
     */
    private lazy final val DEBUG_UPDATE_LINK_EXECUTABLE =
        "http://dlc.sun.com.edgesuite.net/virtualbox/" +
            "4.2.14/VirtualBox-4.2.14-86644-Linux_amd64.run"

    setName(Messages.getString(R.string.updater_service))

    override def run(): Unit = {
        try {
            L.i("%s >> starting", classOf[Updater].getSimpleName())

            /*
             * DOWNLOAD UPDATE.PROPERTIES AND PARSE INFO TO MEMORY
             */
            val updateProperties = downloadUpdateProperties()
            if (updateProperties == null || isInterrupted())
                return

            L.i("\tCurrent version: %,d (%s) -- Update version: %s",
                Sys.APP_VERSION_CODE, Sys.APP_VERSION_NAME,
                updateProperties.getProperty(KEY_APP_VERSION_CODE))

            try {
                if (Sys.APP_VERSION_CODE >= Integer.parseInt(updateProperties
                    .getProperty(KEY_APP_VERSION_CODE)) && !Sys.DEBUG)
                    return
            } catch {
                case t: Throwable =>
                    /*
                     * Can be number format exception or NPE...
                     */
                    return
            }

            L.d("\t>> %s", updateProperties)

            /*
             * CHECK TO SEE IF THE UPDATE FILE HAS BEEN DOWNLOADED BEFORE
             */
            if (isInterrupted() || checklocalUpdateFile(updateProperties))
                return

            /*
             * DOWNLOAD THE UPDATE FILE
             */
            downloadUpdateFile(updateProperties)
        } catch {
            case e: Exception =>
                L.e("%s >> %s", classOf[Updater].getSimpleName(), e)
        } finally {
            L.i("%s >> finishing", classOf[Updater].getSimpleName())
            sendNotification(MSG_DONE)
        }
    } // run()

    /**
     * Follows the redirection ({@ink Network#HTTP_STATUS_FOUND}) within
     * {@link Network#MAX_REDIRECTION_ALLOWED}.
     *
     * @param url
     *            the original URL.
     * @return the last <i>established</i>-connection, maybe {@code null} if
     *         could not connect to. You must always re-check the response code
     *         before doing further actions.
     */
    private def followRedirection(url: String): HttpURLConnection = {
        L.i("%s >> followRedirection() >> %s", classOf[Updater].getSimpleName(),
            url)

        var conn = Network.openHttpConnection(url)
        if (conn == null)
            return null

        var redirectCount = 0
        try {
            conn.connect()
            while (conn.getResponseCode() == Network.HTTP_STATUS_FOUND
                && redirectCount + 1 < Network.MAX_REDIRECTION_ALLOWED) {
                redirectCount += 1
                val inputStream = conn.getInputStream()

                /*
                 * Expiration.
                 */
                var field = conn.getHeaderField(Network.HEADER_EXPIRES)
                try {
                    if (!Texts.isEmpty(field)
                        && Calendar.getInstance().after(
                            new SimpleDateFormat(
                                Network.HEADER_DATE_FORMAT)
                                .parse(field))) {
                        L.i("\t%,d is expired (%s)", conn.getResponseCode(),
                            field)
                        inputStream.close()
                        return null
                    }
                } catch {
                    case e: ParseException =>
                        /*
                         * Ignore it.
                         */
                        L.e("\tcan't parse '%s', ignoring it...", field)
                }

                /*
                 * Location.
                 */
                field = conn.getHeaderField(Network.HEADER_LOCATION)
                if (Texts.isEmpty(field)) {
                    L.i("\t%,d sends to null", conn.getResponseCode())
                    inputStream.close()
                    return null
                }

                /*
                 * Close current connection and open the redirected URI.
                 */
                inputStream.close()
                conn = Network.openHttpConnection(field)
                if (conn == null) return null

                L.i("\t>> %s", field)
                conn.connect()
            } // while
        } catch {
            case e: IOException =>
                // TODO Auto-generated catch block
                e.printStackTrace()
        }

        conn
    } // followRedirection()

    /**
     * Downloads the `update.properties` file from server.
     *
     * @return the {@link Properties} object containing update information. Or
     *         {@code null} if an error occurred.
     */
    private def downloadUpdateProperties(): Properties = {
        L.i("%s >> downloadUpdateProperties()", classOf[Updater].getSimpleName())

        for (url <- URLS_UPDATE_PROPERTIES) {
            var conn = followRedirection(url)
            if (conn == null)
                return null

            try {
                val inputStream = new BufferedInputStream(
                    conn.getInputStream(), Files.FILE_BUFFER)
                try {
                    if (conn.getResponseCode() == Network.HTTP_STATUS_OK &&
                        conn.getContentLength() <= MAX_UPDATE_PROPERTIES_FILESIZE) {
                        /*
                         * We can load directly from the `InputStream` over the
                         * network, since the size is small.
                         */
                        var result = new Properties()
                        result.load(inputStream)
                        return result
                    }
                } finally inputStream.close()
            } catch {
                case e: IOException =>
                    /*
                     * Ignore it. Maybe the current URL doesn't exist. Try the next one.
                     */
                    e.printStackTrace()
                case e: NullPointerException =>
                /*
                 * Ignore it.
                 */
            }
        } // for URL

        null
    } // downloadUpdateProperties()

    /**
     * Checks to see if there is update file which has been downloaded before.
     *
     * @param updateProperties
     *            the update information.
     * @return {@code true} or {@code false}.
     */
    private def checklocalUpdateFile(updateProperties: Properties): Boolean = {
        L.i("%s >> checklocalUpdateFile()", classOf[Updater].getSimpleName())

        var file = new File(Sys.appDir().getAbsolutePath() +
            File.separator +
            Files.fixFilename(updateProperties
                .getProperty(KEY_DOWNLOAD_FILENAME)))
        if (!file.isFile()) return false

        /*
         * Check SHA-1.
         */
        try {
            var md = MessageDigest.getInstance(Hasher.SHA1)

            val buf = new Array[Byte](Files.FILE_BUFFER)
            var read = 0
            val inputStream = new BufferedInputStream(
                new FileInputStream(file), Files.FILE_BUFFER)
            try {
                while ({ read = inputStream.read(buf); read } > 0) {
                    if (isInterrupted()) return false
                    md.update(buf, 0, read)
                }
            } finally inputStream.close()

            var bi = new BigInteger(1, md.digest())
            val result = updateProperties.getProperty(KEY_SHA1)
                .equalsIgnoreCase(
                    ("%0" + (md.digest().length * 2)
                        + "x").format(bi))
            if (result)
                sendNotification(
                    MSG_LOCAL_UPDATE_AVAILABLE,
                    Messages.getString(R.string.msg_local_update_available),
                    Messages.getString(
                        R.string.pmsg_local_update_available, file
                            .getAbsolutePath(),
                        updateProperties
                            .getProperty(KEY_APP_VERSION_NAME)))
            result
        } catch {
            case e: NoSuchAlgorithmException =>
                /*
                 * Never catch this.
                 */
                e.printStackTrace()
                false
            case e: FileNotFoundException =>
                /*
                 * Never catch this.
                 */
                e.printStackTrace()
                false
            case e: IOException =>
                /*
                 * Ignore it.
                 */
                false
            case e: NullPointerException => false
            case _: Exception => false
        }
    } // checklocalUpdateFile()

    /**
     * Downloads the update file.
     *
     * @param updateProperties
     *            the update information.
     */
    private def downloadUpdateFile(updateProperties: Properties): Unit = {
        L.i("%s >> downloadUpdateFile()", classOf[Updater].getSimpleName())

        var conn = followRedirection(
            if (Sys.DEBUG) DEBUG_UPDATE_LINK_EXECUTABLE
            else updateProperties.getProperty(KEY_DOWNLOAD_URI))
        if (conn == null) return

        try {
            val inputStream = conn.getInputStream()
            try {
                if (conn.getResponseCode() != Network.HTTP_STATUS_OK)
                    return

                val contentLength = conn.getContentLength()
                if (contentLength == 0) return
                if (contentLength > 0 && contentLength > MAX_UPDATE_FILESIZE) {
                    sendNotification(
                        MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT,
                        Messages.getString(R.string.msg_cancelled_update),
                        Messages.getString(
                            R.string.pmsg_update_filesize_exceeds_limit,
                            Texts.sizeToStr(contentLength),
                            Texts.sizeToStr(MAX_UPDATE_FILESIZE)))
                    return
                }

                /*
                 * PARSE FILENAME FROM SERVER
                 */

                var fileName: String = null
                val contentDisposition = conn
                    .getHeaderField("Content-Disposition")
                if (Texts.isEmpty(contentDisposition)
                    || !contentDisposition.matches("(?si).*?attachment.+")) {
                    fileName = Files.fixFilename(updateProperties
                        .getProperty(KEY_DOWNLOAD_FILENAME))
                } else {
                    var m = Pattern.compile("(?si)filename=\"?.+?\"?$")
                        .matcher(contentDisposition)
                    if (m.find())
                        fileName = Files.fixFilename(m.group()
                            .replaceFirst("(?si)^filename=\"?", "")
                            .replaceFirst("\"$", ""))
                }
                if (Texts.isEmpty(fileName)) return

                var targetFile = new File(Sys.appDir().getAbsolutePath() +
                    File.separator + fileName)

                L.d("%s >> %s", CLASSNAME, targetFile.getAbsolutePath())

                if (contentLength > 0
                    && targetFile.getParentFile() != null
                    && targetFile.getParentFile().getFreeSpace() <= contentLength * 1.5) {
                    sendNotification(MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT,
                        Messages.getString(R.string.msg_cancelled_update),
                        Messages.getString(
                            R.string.pmsg_available_space_is_low, Texts
                                .sizeToStr(targetFile
                                    .getParentFile()
                                    .getFreeSpace())))
                    return
                }

                /*
                 * START DOWNLOADING
                 */

                val outputStream = new BufferedOutputStream(
                    new FileOutputStream(targetFile), Files.FILE_BUFFER)
                val totalRead = Array(0l)
                val speedTracker = new SpeedTracker()
                val timer = new Timer()
                timer.schedule(new TimerTask() {

                    override def run(): Unit = {
                        if (contentLength > 0) {
                            sendNotification(
                                MSG_UPDATE_PROGRESS,
                                Messages.getString(
                                    R.string.pmsg_updating_with_percentage,
                                    Texts.percentToStr(totalRead(0)
                                        * 100f / contentLength),
                                    Texts.sizeToStr(totalRead(0)),
                                    Texts.sizeToStr(speedTracker
                                        .calcInstantaneousSpeed())))
                        } // contentLength > 0
                        else {
                            sendNotification(MSG_UPDATE_PROGRESS, Messages
                                .getString(R.string.pmsg_updating, Texts
                                    .sizeToStr(totalRead(0)), Texts
                                    .sizeToStr(speedTracker
                                        .calcInstantaneousSpeed())))
                        } // //contentLength == 0
                    } // run()

                }, 999, 999)
                try {
                    val md = MessageDigest
                        .getInstance(Hasher.SHA1)
                    var buf = new Array[Byte](Files.FILE_BUFFER)
                    var read = 0
                    var tick = System.nanoTime()
                    while ({ read = inputStream.read(buf); read } > 0) {
                        if (isInterrupted()) {
                            outputStream.close()
                            targetFile.delete()
                            return
                        }

                        outputStream.write(buf, 0, read)
                        totalRead(0) += read

                        md.update(buf, 0, read)

                        tick = System.nanoTime() - tick
                        if (tick > 0)
                            speedTracker + totalRead(0) / (tick / 1e6)
                        else speedTracker + totalRead(0) / 1e6

                        var freeSpace = 0l
                        if (targetFile.getParentFile() != null) {
                            freeSpace = targetFile.getParentFile()
                                .getFreeSpace()
                            if (freeSpace < totalRead(0) * 1.5) {
                                sendNotification(
                                    MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT,
                                    Messages.getString(R.string.msg_cancelled_update),
                                    Messages.getString(
                                        R.string.pmsg_available_space_is_low,
                                        Texts.sizeToStr(freeSpace)))
                                outputStream.close()
                                targetFile.delete()
                                return
                            }
                        }

                        tick = System.nanoTime()
                    } // while

                    outputStream.close()
                    timer.cancel()

                    /*
                     * CHECK SHA-1
                     */
                    var bi = new BigInteger(1, md.digest())
                    if (updateProperties.getProperty(KEY_SHA1)
                        .equalsIgnoreCase(
                            ("%0"
                                + (md.digest().length * 2) + "x").format(
                                    bi))) {
                        sendNotification(
                            MSG_UPDATE_FINISHED,
                            Messages.getString(R.string.msg_update_finished),
                            Messages.getString(
                                R.string.pmsg_update_finished,
                                targetFile.getAbsolutePath(),
                                updateProperties
                                    .getProperty(KEY_APP_VERSION_NAME)))
                    } else {
                        targetFile.delete()
                        sendNotification(
                            MSG_UPDATE_CANCELLED,
                            Messages.getString(R.string.msg_update_cancelled),
                            Messages.getString(R.string.msg_update_cancelled_because_wrong_checksum))
                    }
                } catch {
                    case e: NoSuchAlgorithmException =>
                    /*
                     * Never catch this.
                     */
                } finally {
                    outputStream.close()
                    timer.cancel()
                }
            } finally {
                if (inputStream != null)
                    inputStream.close()
            }
        } catch {
            case t: Throwable =>
                /*
                 * Ignore it.
                 */
                t.printStackTrace()
        }
    } // downloadUpdateFile()

}
