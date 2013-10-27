/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.Writer
import java.util.Properties
import java.util.UUID

/**
 * Convenient class for storing/ loading preferences.
 *
 * @author Hai Bison
 */
object Preferences {

    /**
     * Used for debugging...
     */
    lazy final val CLASSNAME = classOf[Preferences].getName()

    lazy final val FILE = new File(Sys.appDir().getAbsolutePath()
            + File.separator + Sys.APP_NAME + ".preferences")
    lazy final val mProperties = new Properties()
    lazy var mTransaction: Properties = null

    /*
     * Load preferences from file
     */
    L.d("Preferences file = %s", FILE)
    try {
        var reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(mPropertiesFile), Texts.UTF8));
        try {
            mProperties.load(reader);
        } finally {
            reader.close();
        }
    } catch {
        case E: Exception =>
            L.e("[%s] Error loading preferences: %s", CLASSNAME, e)
    }

    /**
     * Begins a transaction. Currently this method supports only one instance of
     * a transaction. This mean calling this method multiple times has only one
     * affect.
     *
     * @return the instance of this object, to allow chaining multiple calls
     *         into a single statement.
     * @see #endTransaction()
     */
    def beginTransaction(): this.type = synchronized {
        if (mTransaction == null)
            mTransaction = new Properties()
        this
    }// beginTransaction()

    /**
     * Ends a transaction.
     *
     * @see #beginTransaction()
     */
    def endTransaction() = synchronized {
        if (mTransaction == null)
            return

        mProperties.putAll(mTransaction)
        destroyTransaction()
    }// endTransaction()

    /**
     * Cancels a transaction.
     *
     * @see #beginTransaction()
     */
    def cancelTransaction() = synchronized { destroyTransaction() }

    /**
     * Destroys the transaction.
     */
    private def destroyTransaction() = synchronized {
        if (mTransaction == null)
            return

        mTransaction.clear()
        mTransaction = null
    }// destroyTransaction()

    /**
     * Stores all preferences to file.
     */
    def store() = {
        try {
            var writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(mPropertiesFile), Texts.UTF8));
            try {
                mProperties.store(writer, null)
            } finally { writer.close() }
        } catch {
            case e: Exception =>
                L.e("[%s] Error storing preferences: %s", CLASSNAME, e)
        }
    }// store()

    /**
     * Sets a preference.
     *
     * @param k
     *            the key name.
     * @param v
     *            the value of the key. If {@code null}, key {@code k} will be
     *            removed.
     * @return the instance of this object, to allow chaining multiple calls
     *         into a single statement.
     */
    def set(k: String, v: String): this.type = {
        var p = if (mTransaction != null) mTransaction else mProperties

        if (v != null)
            p.setProperty(k, v.trim())
        else
            p.remove(k)

        this
    }// set()

    /**
     * Encrypts and sets a preference.
     *
     * @param k
     *            the key name.
     * @param v
     *            the value of the key. If {@code null}, key {@code k} will be
     *            removed.
     * @return the instance of this object, to allow chaining multiple calls
     *         into a single statement.
     */
    def xSet(k: String, v: String): this.type =
        set(k,
            if (v != null)
                SimpleWeakEncryption.encrypt(getUid().toCharArray(), v)
            else null)

    /**
     * Gets value of a key.
     *
     * @param k
     *            the key name.
     * @return the value of the given key.
     */
    def get(k: String) = get(k, null)

    /**
     * Gets value of a key.
     *
     * @param k
     *            the key name.
     * @param default
     *            the default value if the given key does not exist.
     * @return the value of the given key, or {@code default} if the given key
     *         does not exist.
     */
    def get(String k, String default): String = {
        if (mTransaction != null && mTransaction.containsKey(k))
            mTransaction.getProperty(k, default)
        else
            mProperties.getProperty(k, default)
    }// get()

    /**
     * Gets and decrypts value of a key.
     *
     * @param k
     *            the key name.
     * @return the value of the given key.
     */
    def xGet(k: String) = xGet(k, null)

    /**
     * Gets and decrypts value of a key.
     *
     * @param k
     *            the key name.
     * @param default
     *            the default value if the given key does not exist.
     * @return the value of the given key, or {@code def} if the given key does
     *         not exist.
     */
    def xGet(k: String, default: String): String = {
        var v = get(k)
        if (v == null)
            default
        else
            SimpleWeakEncryption.decrypt(getUid().toCharArray(), v)
    }// xGet()

    /*
     * PREFERENCES
     */

    lazy final val KEY_JDK_PATH = "JDK_PATH"
    lazy final val KEY_LOCALE_TAG = "locale_tag"
    lazy final val KEY_UID = "uid"
    lazy final val KEY_NETWORK_USE_PROXY = "network.use_proxy"
    lazy final val KEY_NETWORK_PROXY_HOST = "network.proxy.host"
    lazy final val KEY_NETWORK_PROXY_PORT = "network.proxy.port"
    lazy final val KEY_NETWORK_PROXY_USERNAME = "network.proxy.username"
    lazy final val KEY_NETWORK_PROXY_PASSWORD = "network.proxy.password"

    /**
     * Gets global unique ID.
     *
     * @return the global unique ID.
     */
    def uid: String = {
        var res = mProperties.getProperty(KEY_UID, null)

        if (Texts.isEmpty(res)) {
            res = UUID.randomUUID().toString()
            /*
             * Don't use set(), to avoid of using transaction...
             */
            mProperties.setProperty(KEY_UID, res)
        }

        res
    }// uid

    /**
     * Gets JDK path.
     *
     * @return the JDK path, or {@code null} if not available.
     */
    def jdkPath: File = {
        var path = get(KEY_JDK_PATH)
        if (path == null) null else new File(path)
    }// jdkPath()

    /**
     * Sets the JDK path.
     *
     * @param path
     *            the JDK path.
     * @return the instance of this object, to allow chaining multiple calls
     *         into a single statement.
     */
    def jdkPath_= (path: File) =
        set(KEY_JDK_PATH, if (path != null) path.getAbsolutePath() else null)

    /**
     * Gets the locale.
     *
     * @return the locale tag, default is {@link Messages#DEFAULT_LOCALE}.
     */
    def localeTag = get(KEY_LOCALE_TAG, Messages.DEFAULT_LOCALE)

    /**
     * Sets the locale tag.
     *
     * @param tag
     *            the locale tag.
     * @return the instance of this object, to allow chaining multiple calls
     *         into a single statement.
     */
    def localeTag_= (tag: String) = set(KEY_LOCALE_TAG, tag)

    /**
     * Checks if we're using a proxy.
     *
     * @return {@code true} or {@code false}.
     */
    def usingProxy = true.toString().equals(
        get(KEY_NETWORK_USE_PROXY, false.toString()))

    /**
     * Sets using proxy.
     *
     * @param v
     *            {@code true} or {@code false}.
     * @return the instance of this object, to allow chaining multiple calls
     *         into a single statement.
     */
    def usingProxy_ = (v: Boolean) = set(KEY_NETWORK_USE_PROXY, v.toString())

    /**
     * Gets the proxy host address.
     *
     * @return the proxy host address.
     */
    def proxyHost = get(KEY_NETWORK_PROXY_HOST)

    /**
     * Sets the proxy host address.
     *
     * @param v
     *            the proxy host address.
     * @return the instance of this object, to allow chaining multiple calls
     *         into a single statement.
     */
    def proxyHost_= (v: String) = set(KEY_NETWORK_PROXY_HOST, v)

    /**
     * Gets the proxy port.
     *
     * @return the proxy port, or {@code -1} if not set.
     */
    def proxyPort: Int = {
        try {
            Integer.parseInt(get(KEY_NETWORK_PROXY_PORT, -1.toString()))
        } catch { case e: Exception => -1 }
    }// getProxyPort()

    /**
     * Sets the proxy port.
     *
     * @param v
     *            the proxy port.
     * @return the instance of this object, to allow chaining multiple calls
     *         into a single statement.
     */
    def proxyPort_= (v: Int) = set(KEY_NETWORK_PROXY_PORT, v.toString())

    /**
     * Gets the proxy username.
     *
     * @return the proxy username.
     */
    def proxyUsername = xGet(KEY_NETWORK_PROXY_USERNAME)

    /**
     * Sets the proxy username.
     *
     * @param v
     *            the proxy username.
     * @return the instance of this object, to allow chaining multiple calls
     *         into a single statement.
     */
    def proxyUsername_= (v: String) = xSet(KEY_NETWORK_PROXY_USERNAME, v)

    /**
     * Gets the proxy password.
     *
     * @return the proxy password.
     */
    def proxyPassword: Array[Char] = {
        var v = xGet(KEY_NETWORK_PROXY_PASSWORD)
        if (v != null) v.toCharArray() else null
    }// proxyPassword

    /**
     * Sets the proxy password.
     *
     * @param v
     *            the proxy password.
     * @return the instance of this object, to allow chaining multiple calls
     *         into a single statement.
     */
    def proxyPassword_= (v: Array[Char]) =
        xSet(KEY_NETWORK_PROXY_PASSWORD, if (v != null) new String(v) else null)

}// Preferences