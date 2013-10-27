/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.ui.prefs

/**
 * The base preferences frame.
 *
 * @author Hai Bison
 *
 */
trait PreferencesFrame {

    /**
     * Stores the preferences.
     *
     * @return {@code true} if storing successfully (validation...). Or
     *         {@code false} otherwise.
     */
    def store(): Boolean

}
