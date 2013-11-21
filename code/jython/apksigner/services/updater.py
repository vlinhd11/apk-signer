#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

import httplib, jarray, json
from basethread import BaseThread

from apksigner.i18n import messages, string
from apksigner.utils import files, system

class Updater(BaseThread):
    '''
    Application updater service.
    '''

    '''
    The URLs pointing to ``update.json`` file. We should try them all (in case
    one of them doesn't exist, or for some reason we can't reach it).
    '''
    URLS_UPDATE_JSON = [
        'https://dl.dropboxusercontent.com/u/237978006/android/apk-signer/update.json',
        'https://bitbucket.org/haibisonapps/apk-signer/downloads/update.json',
        'https://apk-signer.googlecode.com/hg/bin/update.json' ]

    KEY_APP_VERSION_CODE = 'app_version_code'
    KEY_APP_VERSION_NAME = 'app_version_name'
    KEY_DOWNLOAD_URI = 'download_uri'
    KEY_DOWNLOAD_FILENAME = 'download_filename'
    KEY_DOWNLOAD_FILE_SHA1 = 'download_file_sha1'
    KEY_UPDATE_TYPE = 'update_type'

    UPDATE_TYPE_PATCHES = 'patches'
    UPDATE_TYPE_RELEASE = 'release'

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

    def run(self):
        ''' Main jobs.
        '''

        try:
            print(' > {}: starting'.format(Updater.__name__))

            ### DOWNLOAD ``update.json`` AND PARSE INFO TO MEMORY

            for url in self.URLS_UPDATE_JSON:
                if self.is_interrupted(): return

                update = self.download(url)
                if not update: continue

                try: update = json.loads(update)
                except: return
                #.for
            #.download_update_info()

            print('\t> Current version: {} ({}) -- Update version: {}'.format(
                  system.APP_VERSION_CODE, system.APP_VERSION_NAME,
                  update.get(self.KEY_APP_VERSION_CODE))

            try:
                if system.APP_VERSION_CODE >= update[self.KEY_APP_VERSION_CODE]:
                    return
            except: return

            /*
             * CHECK TO SEE IF THE UPDATE FILE HAS BEEN DOWNLOADED BEFORE
             */
            if (isInterrupted() || check_local_update_file(updateProperties))
                return

            /*
             * DOWNLOAD THE UPDATE FILE
             */
            downloadUpdateFile(updateProperties)
        except:
            print(' ! {}: {}'.format(Updater.__name__, sys.exc_info()))
        finally:
            print(' > {}: finishing'.format(Updater.__name__))
            send_notification(self.MSG_DONE)
        #.run()

    def follow_redirection(self, url):
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

    def download(self, url, handler=None):
        ''' Downloads ``url``.

            Parameters:

            :url:
                the URL.
            :handler:
                the function which accepts one parameter (a byte array). If it
                is provided, it will be called to pass through the data
                downloaded (in pieces).

            If ``handler`` is not available, the data downloaded is returned.

            Returns:
                ``None`` if any error occurred or ``handler`` is provided. Or
                the data downloaded if ``handler`` is not provided.
        '''

        conn = self.follow_redirection(url)
        if not conn: return

        if not handler: data = jarray.zeros(0, 'b')

        try:
            conn.connect()
            stream = conn.getInputStream()
        except: return

        try:
            buf = jarray.zeros(1024 * 32, 'b')
            while 1:
                read = stream.read(buf)
                if read <= 0: break
                if handler: handler(buf[:read])
                else: data += buf[:read]
        except: return
        finally:
            try: stream.close()
            except: return

        if not handler: return data
        #.download()

    def check_local_update_file(update_info):
        ''' Checks to see if there is update file which has been downloaded
            before.

            Parameters:

            :update_info:
                the update information.

            Returns:
                ``True`` or ``None``.
        '''

        print('{} >> check_local_update_file()'.format(Updater.__name__))

        filename = os.path.join(os.path.dirname(sys.argv[0]),
                                update_info[KEY_DOWNLOAD_FILENAME])
        if os.path.isfile(filename):
            # Check SHA-1
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
        #.check_local_update_file()

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
    }/ downloadUpdateFile()

}
