/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.services

/**
 * Interface for notification.
 *
 * @author Hai Bison
 * @since v1.6 beta
 */
trait INotification {

    /**
     * Will be called when there is new message.
     *
     * @param msg
     *            the message.
     * @return {@code true} to handle the message yourself, {@code false} to let
     *         the message sender continue its job.
     */
    def onMessage(msg: Message): Boolean

}