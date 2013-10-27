/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

/**
 * Logger.
 *
 * @author Hai Bison
 * @since v1.9 beta
 */
object L {

    /**
     * Prints log to standard output console.
     *
     * @param tag
     *            the tag.
     * @param msg
     *            the message.
     * @param args
     *            the objects to be formatted with {@code msg}.
     */
    def out(tag: Any, msg: String, args: Any*) =
        printf("[%s] %s\n", tag, msg.format(args : _*))

    /**
     * Prints log to error output console.
     *
     * @param tag
     *            the tag.
     * @param msg
     *            the message.
     * @param args
     *            the objects to be formatted with {@code msg}.
     */
    def err(tag: Any, msg: String, args: Any*) =
        System.err.println("[%s] %s".format(tag, msg.format(args : _*)))

    /**
     * Prints debug log.
     *
     * @param msg
     *            the message.
     * @param args
     *            the objects to be formatted with {@code msg}.
     */
    def d(msg: String, args: Any*) = out("DEBUG", msg, args : _*)

    /**
     * Prints information log.
     *
     * @param msg
     *            the message.
     * @param args
     *            the objects to be formatted with {@code msg}.
     */
    def i(msg: String, args: Any*) = out("INFO", msg, args : _*)

    /**
     * Prints "verbose" log.
     *
     * @param msg
     *            the message.
     * @param args
     *            the objects to be formatted with {@code msg}.
     */
    def v(msg: String, args: Any*) = out("VERBOSE", msg, args : _*)

    /**
     * Prints error log.
     *
     * @param msg
     *            the message.
     * @param args
     *            the objects to be formatted with {@code msg}.
     */
    def e(msg: String, args: Any*) = err("ERROR", msg, args : _*)

    /**
     * Prints the stack trace.
     *
     * @param t
     *            an exception.
     * @return the stack trace.
     */
    def printStackTrace(t: Throwable): CharSequence = {
        var stringWriter = new StringWriter()
        var printWriter = new PrintWriter(stringWriter)
        try {
            t.printStackTrace(printWriter)
            stringWriter.toString()
        } finally {
            printWriter.close()
        }
    }// printStackTrace()

}// L