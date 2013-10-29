/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.i18n

import java.lang.reflect.Modifier
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.Map

import group.pals.desktop.app.apksigner.utils.Preferences

/**
 * Manager for i18n strings.
 *
 * @author Hai Bison
 * @since v1.6 beta
 */
object Messages {

    /**
     * Default locale.
     */
    lazy final val DEFAULT_LOCALE = "en"

    /**
     * Map of available locale tags to their human readable names.
     */
    lazy final val AVAILABLE_LOCALES = LinkedHashMap[String, String]()

    AVAILABLE_LOCALES += DEFAULT_LOCALE -> "English (Default)"
    AVAILABLE_LOCALES += "vi" -> "Vietnamese (Tiếng Việt)"

    private lazy final val BUNDLE_NAME = "group.pals.desktop.app.apksigner.i18n.messages" //$NON-NLS-1$
    private lazy final val RESOURCE_BUNDLE = loadBundle()
    private var mDefaultResourceBundle: ResourceBundle = null

    /**
     * Loads message resource.
     *
     * @return the resource bundle which contains message resource.
     */
    private def loadBundle(): ResourceBundle = {
        var localeTag = Preferences.localeTag
        if (!AVAILABLE_LOCALES.contains(localeTag)) {
            Preferences.localeTag = DEFAULT_LOCALE
            localeTag = DEFAULT_LOCALE
        }
        ResourceBundle.getBundle(BUNDLE_NAME, Locale.forLanguageTag(localeTag))
    } // loadBundle()

    /**
     * Gets a string for current locale by its key. If it's not available for
     * current locale, the default string in built-in locale (English) will
     * return.
     *
     * @param key
     *            the string's key.
     * @return the string, or {@code null} if not found.
     */
    private def getString(key: String): String = {
        try {
            if (RESOURCE_BUNDLE.containsKey(key))
                return RESOURCE_BUNDLE.getString(key)

            /*
             * Try to find the `key` in default locale.
             */
            if (!new Locale(DEFAULT_LOCALE).getLanguage().equals(
                RESOURCE_BUNDLE.getLocale().getLanguage())) {
                if (mDefaultResourceBundle == null)
                    mDefaultResourceBundle = ResourceBundle.getBundle(
                        BUNDLE_NAME, Locale.forLanguageTag(DEFAULT_LOCALE))
                return mDefaultResourceBundle.getString(key)
            }

            null
        } catch {
            case e: MissingResourceException => null
        }
    } // getString()

    /**
     * Map of resources IDs to their name.
     */
    private lazy final val MAP_IDS = Map[Int, String]()

    /**
     * Gets a string by its resource ID. If it's not available for current
     * locale, the default string in built-in locale (English) will return.
     *
     * @param resId
     *            the resource ID.
     * @param ars
     *            if provided, will be used to format the string resource.
     * @return the string, or {@code null} if not found.
     */
    def getString(resId: Int, args: Any*): String = {
        MAP_IDS.get(resId) match {
            case Some(s) => return s.format(args: _*)
            case None =>
        }

        for (f <- R.string.getClass().getFields()) {
            if (Modifier.isStatic(f.getModifiers())
                && f.getType().isAssignableFrom(Integer.TYPE)) {
                try {
                    if (f.getInt(null) == resId) {
                        MAP_IDS += resId -> f.getName()
                        return getString(f.getName()).format(args: _*)
                    }
                } catch {
                    case e: IllegalArgumentException =>
                        /*
                        * Never catch this, we checked the object type before.
                        */
                        e.printStackTrace()
                    case e: IllegalAccessException =>
                        /*
                        * Never catch this, since we asked for all *public* fields.
                        */
                        e.printStackTrace()
                }
            } // if
        } // for

        null
    } // getString()

}
