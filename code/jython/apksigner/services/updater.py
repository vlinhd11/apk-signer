#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

import java
import json

from apksigner.i18n import messages, string
from apksigner.utils import files
from basethread import BaseThread

import group.pals.desktop.app.apksigner.utils.Files
import group.pals.desktop.app.apksigner.utils.Hasher
import group.pals.desktop.app.apksigner.utils.L
import group.pals.desktop.app.apksigner.utils.Network
import group.pals.desktop.app.apksigner.utils.SpeedTracker
import group.pals.desktop.app.apksigner.utils.Sys
import group.pals.desktop.app.apksigner.utils.Texts

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
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
import java.util.regex.Matcher
import java.util.regex.Pattern

class Updater(BaseThread):
    '''
    Application updater service.
    '''

    '''
    The URLs pointing to ``update.json`` file. We should try them all (in case
    one of them doesn't exist, or for some reason we can't reach it).
    '''
    URLS_UPDATE_JSON = [
        'http://dl.bintray.com/hai%20bison%20apps/android/apk-signer/update.json',
        'https://bitbucket.org/haibisonapps/apk-signer/downloads/update.json',
        'https://sites.google.com/site/haibisonapps/apps/apk-signer/update.json' ]

    KEY_APP_VERSION_CODE = 'app_version_code'
    KEY_APP_VERSION_NAME = 'app_version_name'
    KEY_DOWNLOAD_URI = 'download_uri'
    KEY_DOWNLOAD_FILENAME = 'download_filename'
    KEY_DOWNLOAD_FILE_SHA1 = 'download_file_sha1'

    ''' Maximum filesize allowed for the new version (``50 MiB``). '''
    MAX_UPDATE_FILESIZE = 50 * 1024 * 1024

    ''' Maximum filesize allowed for the ``update.json`` (``64 KiB``). '''
    MAX_UPDATE_JSON_FILESIZE = 64 * 1024

    ''' There is a local update file available. '''
    MSG_LOCAL_UPDATE_AVAILABLE = 1

    ''' The update filesize exceeds limit. '''
    MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT = 2

    ''' The update progress (percentage done...) '''
    MSG_UPDATE_PROGRESS = 3

    ''' The update cancelled. '''
    MSG_UPDATE_CANCELLED = 4

    ''' The update finished. '''
    MSG_UPDATE_FINISHED = 5

    def __init__(self):
        ''' Creates new instance.
        '''

        super(Updater, self).__init__(
            messages.get_string(string.updater_service))
        #.__init__()

    def run():
        try:
            print('{} >> starting'.format(Updater.__name__))

            ### DOWNLOAD UPDATE.PROPERTIES AND PARSE INFO TO MEMORY
            final Properties updateProperties = download_update_info()
            if (updateProperties == null || isInterrupted())
                return

            print("\tCurrent version: %,d (%s) -- Update version: %s",
                    Sys.APP_VERSION_CODE, Sys.APP_VERSION_NAME,
                    updateProperties.getProperty(KEY_APP_VERSION_CODE))

            try {
                if (Sys.APP_VERSION_CODE >= Integer.parseInt(updateProperties
                        .getProperty(KEY_APP_VERSION_CODE)) && !Sys.DEBUG)
                    return
            } catch (Throwable t) {
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
        } catch (Exception e) {
            L.e("%s >> %s", Updater.class.getSimpleName(), e)
        } finally {
            print("%s >> finishing", Updater.class.getSimpleName())
            sendNotification(MSG_DONE)
        }
    }// run()

    def follow_redirection(url):
        ''' Follows the redirection ``httplib.FOUND`` within
            ``network.MAX_REDIRECTION_ALLOWED``.

            Parameters:

            :url:
                the original URL.

            Returns:
                the last *established*-connection, maybe ``None`` if could not
                connect to. You must always re-check the response code before
                doing further actions.
        '''

        print('{} >> follow_redirection() >> {}'.format(Updater.__name__, url))

        conn = network.open_java_url(url)
        if not conn: return

        import httplib
        redirect_count = 0
        try:
            conn.connect()
            while conn.getResponseCode() == httplib.FOUND and \
                redirect_count + 1 < network.MAX_REDIRECTION_ALLOWED:
                input_stream = conn.getInputStream()

                # Expiration
                field = conn.getHeaderField(network.HEADER_EXPIRES)
                try:
                    from java.text import SimpleDateFormat
                    from java.util import Calendar
                    if field and \
                        Calendar.getInstance().after(
                            SimpleDateFormat(network.HEADER_DATE_FORMAT).\
                                parse(field)):
                        print('\t{} -- expired ({})'.\
                              format(conn.getResponseCode(), field))
                        input_stream.close()
                        return
                except:
                    # Shoud be ParseException, ignore it
                    print('\tcan\'t parse "{}", ignoring it...'.format(field))

                # Location
                field = conn.getHeaderField(network.HEADER_LOCATION)
                if not field:
                    print('\t{} >> sends to null'.format(conn.getResponseCode()))
                    input_stream.close()
                    return

                # Close current connection and open the redirected URI
                input_stream.close()
                conn = network.open_java_url(field))
                if not conn: return

                print('\t>> {}'.format(field))
                conn.connect()

                redirect_count += 1
                #.while
        except:
            # TODO
            pass

        return conn
        #.follow_redirection()

    def download_update_info():
        ''' Downloads the `update.json` file from server.

            Returns:
                the dictionary (parsing from the JSON), or ``None`` if an error
                occurred.
        '''

        print('{} >> download_update_info()'.format(Updater.__name__))

        for url in URLS_UPDATE_JSON:
            conn = follow_redirection(url)
            if not conn: return

            try:
                import java
                input_stream = java.io.BufferedInputStream(
                    conn.getInputStream(), files.FILE_BUFFER)
                try:
                    if conn.getResponseCode() != network.OK:
                        continue
                    length = conn.getContentLength()
                    if length > MAX_UPDATE_JSON_FILESIZE:
                        continue

                    # We can load directly from the `InputStream` over the
                    # network, since the size is small.
                    import jarray
                    buf = jarray.zeros(length, 'b')
                    if input_stream.read(buf) == length:
                        try: return json.loads(buf.tostring())
                        except: return
                    return
                finally:
                    input_stream.close()
            except:
                # Ignore it. Maybe the current URL doesn't exist. Try the next
                # one.
                continue
            #.for
        #.download_update_info()

    /**
     * Checks to see if there is update file which has been downloaded before.
     *
     * @param updateProperties
     *            the update information.
     * @return {@code true} or {@code false}.
     */
    private boolean checklocalUpdateFile(Properties updateProperties) {
        print("%s >> checklocalUpdateFile()", Updater.class.getSimpleName())

        File file = new File(Sys.getAppDir().getAbsolutePath()
                + File.separator
                + Files.fixFilename(updateProperties
                        .getProperty(KEY_DOWNLOAD_FILENAME)))
        if (file.isFile()) {
            /*
             * Check SHA-1.
             */
            try {
                MessageDigest md = MessageDigest.getInstance(Hasher.SHA1)

                final byte[] buf = new byte[Files.FILE_BUFFER]
                int read
                final InputStream input_stream = new BufferedInputStream(
                        new FileInputStream(file), Files.FILE_BUFFER)
                try {
                    while ((read = input_stream.read(buf)) > 0) {
                        if (isInterrupted())
                            return false
                        md.update(buf, 0, read)
                    }
                } finally {
                    input_stream.close()
                }

                BigInteger bi = new BigInteger(1, md.digest())
                final boolean result = updateProperties.getProperty(KEY_SHA1)
                        .equalsIgnoreCase(
                                String.format("%0" + (md.digest().length * 2)
                                        + "x", bi))
                if (result)
                    sendNotification(
                            MSG_LOCAL_UPDATE_AVAILABLE,
                            messages.get_string(string.msg_local_update_available),
                            messages.get_string(
                                    string.pmsg_local_update_available, file
                                            .getAbsolutePath(),
                                    updateProperties
                                            .getProperty(KEY_APP_VERSION_NAME)))
                return result
            } catch (NoSuchAlgorithmException e) {
                /*
                 * Never catch this.
                 */
                e.printStackTrace()
                return false
            } catch (FileNotFoundException e) {
                /*
                 * Never catch this.
                 */
                e.printStackTrace()
                return false
            } catch (IOException e) {
                /*
                 * Ignore it.
                 */
                return false
            } catch (NullPointerException e) {
                return false
            }
        }// file.isFile()
        else
            return false
    }// checklocalUpdateFile()

    /**
     * Downloads the update file.
     *
     * @param updateProperties
     *            the update information.
     */
    private void downloadUpdateFile(Properties updateProperties) {
        print("%s >> downloadUpdateFile()", Updater.class.getSimpleName())

        HttpURLConnection conn = follow_redirection(Sys.DEBUG ? DEBUG_UPDATE_LINK_EXECUTABLE
                : updateProperties.getProperty(KEY_DOWNLOAD_URI))
        if (conn == null)
            return

        try {
            final InputStream input_stream = conn.getInputStream()
            try {
                if (conn.getResponseCode() != network.HTTP_STATUS_OK)
                    return

                final int contentLength = conn.getContentLength()
                if (contentLength == 0)
                    return
                if (contentLength > 0 && contentLength > MAX_UPDATE_FILESIZE) {
                    sendNotification(
                            MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT,
                            messages.get_string(string.msg_cancelled_update),
                            messages.get_string(
                                    string.pmsg_update_filesize_exceeds_limit,
                                    Texts.sizeToStr(contentLength),
                                    Texts.sizeToStr(MAX_UPDATE_FILESIZE)))
                    return
                }

                /*
                 * PARSE FILENAME FROM SERVER
                 */

                String fileName = null
                final String contentDisposition = conn
                        .getHeaderField("Content-Disposition")
                if (Texts.isEmpty(contentDisposition)
                        || !contentDisposition.matches("(?si).*?attachment.+")) {
                    fileName = Files.fixFilename(updateProperties
                            .getProperty(KEY_DOWNLOAD_FILENAME))
                } else {
                    Matcher m = Pattern.compile("(?si)filename=\"?.+?\"?$")
                            .matcher(contentDisposition)
                    if (m.find())
                        fileName = Files.fixFilename(m.group()
                                .replaceFirst("(?si)^filename=\"?", "")
                                .replaceFirst("\"$", ""))
                }
                if (Texts.isEmpty(fileName))
                    return

                File targetFile = new File(Sys.getAppDir().getAbsolutePath()
                        + File.separator + fileName)

                L.d("%s >> %s", CLASSNAME, targetFile.getAbsolutePath())

                if (contentLength > 0
                        && targetFile.getParentFile() != null
                        && targetFile.getParentFile().getFreeSpace() <= contentLength * 1.5) {
                    sendNotification(MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT,
                            messages.get_string(string.msg_cancelled_update),
                            messages.get_string(
                                    string.pmsg_available_space_is_low, Texts
                                            .sizeToStr(targetFile
                                                    .getParentFile()
                                                    .getFreeSpace())))
                    return
                }

                /*
                 * START DOWNLOADING
                 */

                final OutputStream outputStream = new BufferedOutputStream(
                        new FileOutputStream(targetFile), Files.FILE_BUFFER)
                final long[] totalRead = { 0 }
                final SpeedTracker speedTracker = new SpeedTracker()
                final Timer timer = new Timer()
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        if (contentLength > 0) {
                            sendNotification(
                                    MSG_UPDATE_PROGRESS,
                                    messages.get_string(
                                            string.pmsg_updating_with_percentage,
                                            Texts.percentToStr(totalRead[0]
                                                    * 100f / contentLength),
                                            Texts.sizeToStr(totalRead[0]),
                                            Texts.sizeToStr(speedTracker
                                                    .calcInstantaneousSpeed())))
                        }// contentLength > 0
                        else {
                            sendNotification(MSG_UPDATE_PROGRESS, messages
                                    .get_string(string.pmsg_updating, Texts
                                            .sizeToStr(totalRead[0]), Texts
                                            .sizeToStr(speedTracker
                                                    .calcInstantaneousSpeed())))
                        }// //contentLength == 0
                    }// run()
                }, 999, 999)
                try {
                    final MessageDigest md = MessageDigest
                            .getInstance(Hasher.SHA1)
                    byte[] buf = new byte[Files.FILE_BUFFER]
                    int read
                    long tick = System.nanoTime()
                    while ((read = input_stream.read(buf)) > 0) {
                        if (isInterrupted()) {
                            outputStream.close()
                            targetFile.delete()
                            return
                        }

                        outputStream.write(buf, 0, read)
                        totalRead[0] += read

                        md.update(buf, 0, read)

                        tick = System.nanoTime() - tick
                        speedTracker.add(tick > 0 ? totalRead[0] / (tick / 1e6)
                                : totalRead[0] / 1e6)

                        long freeSpace = 0
                        if (targetFile.getParentFile() != null) {
                            freeSpace = targetFile.getParentFile()
                                    .getFreeSpace()
                            if (freeSpace < totalRead[0] * 1.5) {
                                sendNotification(
                                        MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT,
                                        messages.get_string(string.msg_cancelled_update),
                                        messages.get_string(
                                                string.pmsg_available_space_is_low,
                                                Texts.sizeToStr(freeSpace)))
                                outputStream.close()
                                targetFile.delete()
                                return
                            }
                        }

                        tick = System.nanoTime()
                    }// while

                    outputStream.close()
                    timer.cancel()

                    /*
                     * CHECK SHA-1
                     */
                    BigInteger bi = new BigInteger(1, md.digest())
                    if (updateProperties.getProperty(KEY_SHA1)
                            .equalsIgnoreCase(
                                    String.format("%0"
                                            + (md.digest().length * 2) + "x",
                                            bi))) {
                        sendNotification(
                                MSG_UPDATE_FINISHED,
                                messages.get_string(string.msg_update_finished),
                                messages.get_string(
                                        string.pmsg_update_finished,
                                        targetFile.getAbsolutePath(),
                                        updateProperties
                                                .getProperty(KEY_APP_VERSION_NAME)))
                    } else {
                        targetFile.delete()
                        sendNotification(
                                MSG_UPDATE_CANCELLED,
                                messages.get_string(string.msg_update_cancelled),
                                messages.get_string(string.msg_update_cancelled_because_wrong_checksum))
                    }
                } catch (NoSuchAlgorithmException e) {
                    /*
                     * Never catch this.
                     */
                } finally {
                    outputStream.close()
                    timer.cancel()
                }
            } finally {
                if (input_stream != null)
                    input_stream.close()
            }
        } catch (Throwable t) {
            /*
             * Ignore it.
             */
            t.printStackTrace()
        }
    }// downloadUpdateFile()
}
