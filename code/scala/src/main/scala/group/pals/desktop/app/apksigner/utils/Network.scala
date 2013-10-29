/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

import android.util.Base64

/**
 * Network utilities.
 *
 * @author Hai Bison
 * @since v1.6 beta
 */
object Network {

    /**
     * The network timeout, in milliseconds.
     */
    lazy final val NETWORK_TIMEOUT = 15000

    /**
     * HTTP status code OK.
     */
    lazy final val HTTP_STATUS_OK = 200

    /**
     * The redirection status code {@code 302}.
     */
    lazy final val HTTP_STATUS_FOUND = 302

    /**
     * Max redirection allowed.
     */
    lazy final val MAX_REDIRECTION_ALLOWED = 9

    /**
     * Header field "Location".
     */
    lazy final val HEADER_LOCATION = "Location"

    /**
     * The date format of header fields.
     */
    lazy final val HEADER_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z"

    /**
     * Header field "Expires".
     */
    lazy final val HEADER_EXPIRES = "Expires"

    private lazy final val PROPERTY_SYS_HTTP_PROXY_HOST = "http.proxyHost"
    private lazy final val PROPERTY_SYS_HTTP_PROXY_PORT = "http.proxyPort"

    private lazy final val PROPERTY_SYS_HTTPS_PROXY_HOST = "https.proxyHost"
    private lazy final val PROPERTY_SYS_HTTPS_PROXY_PORT = "https.proxyPort"

    /**
     * Opens new connection to {@code url} with default settings.
     *
     * @param url
     *            the URL.
     * @return the connection. Or {@code null} if an error occurred.
     */
    def openHttpConnection(url: String): HttpURLConnection = {
        if (Texts.isEmpty(url))
            return null

        if (Preferences.usingProxy) {
            /*
             * HTTP
             */
            System.setProperty(PROPERTY_SYS_HTTP_PROXY_HOST,
                Preferences.proxyHost)
            System.setProperty(PROPERTY_SYS_HTTP_PROXY_PORT,
                Preferences.proxyPort.toString())

            /*
             * HTTPS
             */
            System.setProperty(PROPERTY_SYS_HTTPS_PROXY_HOST,
                Preferences.proxyHost)
            System.setProperty(PROPERTY_SYS_HTTPS_PROXY_PORT,
                Preferences.proxyPort.toString())
        } else {
            for (
                s <- Array(PROPERTY_SYS_HTTP_PROXY_HOST,
                    PROPERTY_SYS_HTTP_PROXY_PORT,
                    PROPERTY_SYS_HTTPS_PROXY_HOST,
                    PROPERTY_SYS_HTTPS_PROXY_PORT)
            ) System.clearProperty(s)
        }

        try {
            var conn = new URL(url).openConnection().asInstanceOf[HttpURLConnection]

            if (Preferences.usingProxy
                && !Texts.isEmpty(Preferences.proxyUsername)) {
                var password = Preferences.proxyPassword
                var proxyAuthorization =
                    Base64.encodeToString(
                        (Preferences.proxyUsername + ":"
                            + (if (password != null) new String(password) else ""))
                            .getBytes(Texts.UTF8),
                        Base64.NO_WRAP)
                // https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
                conn.setRequestProperty("Proxy-Authorization", "Basic "
                    + proxyAuthorization)
            }

            conn.setConnectTimeout(NETWORK_TIMEOUT)
            conn.setReadTimeout(NETWORK_TIMEOUT)
            conn
        } catch {
            case e: MalformedURLException =>
                e.printStackTrace()
                null
            case e: IOException =>
                /*
                * Perhaps there is no available Internet connections.
                */
                e.printStackTrace()
                null
            case e: Exception =>
                e.printStackTrace()
                null
        }
    } // openHttpConnection()

}
