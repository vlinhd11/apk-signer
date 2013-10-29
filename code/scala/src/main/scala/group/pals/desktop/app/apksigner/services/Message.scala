/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.services

/**
 * The message.
 *
 * @param id
 *            The message ID.
 * @param obj
 *            The arbitrary object.
 * @param shortMessage
 *            The short message.
 * @param detailedMessage
 *            The detailed message.
 * @author Hai Bison
 * @since v1.6 beta
 */
class Message(var id: Int, var obj: Any = null, var shortMessage: String = null,
              var detailedMessage: String = null) {}