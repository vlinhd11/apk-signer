/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.services

import scala.collection.mutable.LinkedHashMap

/**
 * The background service manager.
 *
 * @author Hai Bison
 * @since v1.6.9 beta
 */
object ServiceManager {

    /**
     * This holds all active threads mapping to their notifications.
     */
    private lazy final val THREADS = LinkedHashMap[BaseThread, INotification]()

    /**
     * Register a thread.
     *
     * @param thread
     *            the thread to register.
     */
    def registerThread(thread: BaseThread): Unit = {
        var notification = new INotification() {

            override def onMessage(msg: Message): Boolean = {
                if (msg.id == BaseThread.MSG_DONE)
                    THREADS -= thread
                false
            } // onMessage()
        }
        thread.addNotification(notification)

        THREADS += thread -> notification
    } // registerThread()

    /**
     * Unregister a thread.
     *
     * @param thread
     *            the thread to unregister.
     * @return {@code true} if the {@code thread} existed and has been
     *         unregistered. {@code false} otherwise.
     */
    def unregisterThread(thread: BaseThread): Boolean = synchronized {
        val notification = THREADS(thread)
        THREADS -= thread
        if (notification != null)
            thread.removeNotification(notification)
        return notification != null
    } // unregisterThread()

    /**
     * Gets the <i>snapshot</i> set of active threads. The order of threads are
     * kept as-is like when they were registered.
     *
     * @return the <i>snapshot</i> set of active threads.
     */
    def activeThreads() = THREADS.keySet

}
