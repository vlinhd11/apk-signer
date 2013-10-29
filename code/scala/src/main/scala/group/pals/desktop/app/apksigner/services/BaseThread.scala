/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.services

import group.pals.desktop.app.apksigner.utils.Texts

/**
 * The base thread.
 *
 * @author Hai Bison
 * @since v1.6.9 beta
 */
object BaseThread {

    /**
     * The thread has done.
     */
    lazy final val MSG_DONE = 0

    /**
     * There is an information.
     */
    lazy final val MSG_INFO = -1

    /**
     * A warning.
     */
    lazy final val MSG_WARNING = -2

    /**
     * An error.
     */
    lazy final val MSG_ERROR = -3

} // BaseThread

/**
 * The base thread.
 *
 * @author Hai Bison
 * @since v1.6.9 beta
 */
class BaseThread extends Thread {

    /**
     * All client notifications.
     */
    private var mNotifications = List[INotification]()

    /**
     * Adds new notification.
     *
     * @param notification
     *            the notification.
     * @return the instance of this thread, for chaining multiple calls into a
     *         single statement.
     */
    def addNotification(notification: INotification): BaseThread = {
        mNotifications :+= notification
        this
    } // addNotification()

    /**
     * Removes a notification.
     *
     * @param notification
     *            the notification to remove.
     */
    def removeNotification(notification: INotification): Unit =
        mNotifications = mNotifications.filter(_ != notification)

    /**
     * Sends notification to all listeners.
     *
     * @param msgId
     *            the message ID.
     * @param obj
     *            your arbitrary object.
     * @param shortMsg
     *            the short message.
     * @param detailedMsg
     *            the detailed message.
     * @return {@code true} if any of the listeners handled the message,
     *         {@code false} otherwise.
     */
    protected def sendNotification(msgId: Int, obj: Any, shortMsg: String,
                                   detailedMsg: String): Boolean = {
        if (mNotifications.isEmpty) return false

        val msg = new Message(msgId, obj, shortMsg, detailedMsg)
        for (notification <- mNotifications)
            if (notification.onMessage(msg)) return true

        false
    } // sendNotification()

    /**
     * Sends notification to all listeners.
     *
     * @param msgId
     *            the message ID.
     * @param obj
     *            your arbitrary object.
     * @param shortMsg
     *            the short message.
     * @return {@code true} if any of the listeners handled the message,
     *         {@code false} otherwise.
     */
    protected def sendNotification(msgId: Int, obj: Object,
                                   shortMsg: String): Boolean =
        sendNotification(msgId, obj, shortMsg, Texts.NULL)

    /**
     * Sends notification to all listeners.
     *
     * @param msgId
     *            the message ID.
     * @param obj
     *            your arbitrary object.
     * @return {@code true} if any of the listeners handled the message,
     *         {@code false} otherwise.
     */
    protected def sendNotification(msgId: Int, obj: Object): Boolean =
        sendNotification(msgId, obj, Texts.NULL)

    /**
     * Sends notification to all listeners.
     *
     * @param msgId
     *            the message ID.
     * @param shortMsg
     *            the short message.
     * @param detailedMsg
     *            the detailed message.
     * @return {@code true} if any of the listeners handled the message,
     *         {@code false} otherwise.
     */
    protected def sendNotification(msgId: Int, shortMsg: String,
                                   detailedMsg: String): Boolean =
        sendNotification(msgId, null, shortMsg, detailedMsg)

    /**
     * Sends notification to all listeners.
     *
     * @param msgId
     *            the message ID.
     * @param shortMsg
     *            the short message.
     * @return {@code true} if any of the listeners handled the message,
     *         {@code false} otherwise.
     */
    protected def sendNotification(msgId: Int, shortMsg: String): Boolean =
        sendNotification(msgId, null, shortMsg, Texts.NULL)

    /**
     * Sends notification to all listeners.
     *
     * @param msgId
     *            the message ID.
     * @return {@code true} if any of the listeners handled the message,
     *         {@code false} otherwise.
     */
    protected def sendNotification(msgId: Int): Boolean =
        sendNotification(msgId, Texts.NULL)

}// BaseThread
