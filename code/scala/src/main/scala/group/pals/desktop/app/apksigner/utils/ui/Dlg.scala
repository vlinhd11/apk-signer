/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils.ui

import java.awt.Component
import java.awt.Dimension

import group.pals.desktop.app.apksigner.i18n.Messages
import group.pals.desktop.app.apksigner.i18n.R
import group.pals.desktop.app.apksigner.utils.Texts
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.SwingUtilities

/**
 * Utilities for dialog boxes.
 *
 * @author Hai Bison
 * @since v1.6 beta
 */
object Dlg {

    /**
     * Shows an error message.
     *
     * @param comp
     *            the root component.
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     */
    def showErrMsg(comp: Component, title: String, msg: Object) = {
        JOptionPane.showMessageDialog(
            comp, msg,
            if (Texts.isEmpty(title)) Messages.getString(R.string.error)
            else title,
            JOptionPane.ERROR_MESSAGE)
    } // showErrMsg()

    /**
     * Shows an error message asynchronously.
     *
     * @param comp
     *            the root component.
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     */
    def showErrMsgAsync(comp: Component, title: String, msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showErrMsg(comp, title, msg)

        })
    } // showErrMsgAsync()

    /**
     * Shows an error message.
     *
     * @param comp
     *            the root component.
     * @param msg
     *            the message.
     */
    def showErrMsg(comp: Component, msg: Object): Unit = showErrMsg(comp, null, msg)

    /**
     * Shows an error message asynchronously.
     *
     * @param comp
     *            the root component.
     * @param msg
     *            the message.
     */
    def showErrMsgAsync(comp: Component, msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showErrMsg(comp, msg)

        })
    } // showErrMsgAsync()

    /**
     * Shows an error message.
     *
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     */
    def showErrMsg(title: String, msg: Object): Unit = showErrMsg(null, title, msg)

    /**
     * Shows an error message asynchronously.
     *
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     */
    def showErrMsgAsync(title: String, msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showErrMsg(title, msg)

        })
    } // showErrMsgAsync()

    /**
     * Shows an error message.
     *
     * @param msg
     *            the message.
     */
    def showErrMsg(msg: Object): Unit = showErrMsg(null, null, msg)

    /**
     * Shows an error message asynchronously.
     *
     * @param msg
     *            the message.
     */
    def showErrMsgAsync(msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showErrMsg(msg)

        })
    } // showErrMsgAsync()

    /**
     * Shows an exception message.
     *
     * @param comp
     *            the root component.
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param e
     *            the exception.
     */
    def showException(comp: Component, title: String, e: Exception) = {
        var msg = Messages.getString(
            R.string.pmsg_exception, e.getClass().getName(), e.getMessage())
        showErrMsg(comp, title, msg)
    } // showException()

    /**
     * Shows an exception message asynchronously.
     *
     * @param comp
     *            the root component.
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param e
     *            the exception.
     */
    def showExceptionAsync(comp: Component, title: String, e: Exception) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showException(comp, title, e)

        })
    } // showExceptionAsync()

    /**
     * Shows an exception message.
     *
     * @param comp
     *            the root component.
     * @param e
     *            the exception.
     */
    def showException(comp: Component, e: Exception): Unit = showException(comp, null, e)

    /**
     * Shows an exception message asynchronously.
     *
     * @param comp
     *            the root component.
     * @param e
     *            the exception.
     */
    def showExceptionAsync(comp: Component, e: Exception) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showException(comp, e)

        })
    } // showExceptionAsync()

    /**
     * Shows an exception message.
     *
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param e
     *            the exception.
     */
    def showException(title: String, e: Exception): Unit = showException(null, title, e)

    /**
     * Shows an exception message asynchronously.
     *
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param e
     *            the exception.
     */
    def showExceptionAsync(title: String, e: Exception) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showException(title, e)

        })
    } // showExceptionAsync()

    /**
     * Shows an exception message.
     *
     * @param e
     *            the exception.
     */
    def showException(e: Exception): Unit = showException(null, null, e)

    /**
     * Shows an exception message asynchronously.
     *
     * @param e
     *            the exception.
     */
    def showExceptionAsync(e: Exception) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showException(e)

        })
    } // showExceptionAsync()

    /**
     * Shows an information message.
     *
     * @param comp
     *            the root component.
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     */
    def showInfoMsg(comp: Component, title: String, msg: Object) = {
        JOptionPane.showMessageDialog(
            comp,
            msg,
            if (Texts.isEmpty(title)) Messages.getString(R.string.information)
            else title,
            JOptionPane.INFORMATION_MESSAGE)
    } // showInfoMsg()

    /**
     * Shows an information message asynchronously.
     *
     * @param comp
     *            the root component.
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     */
    def showInfoMsgAsync(comp: Component, title: String, msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showInfoMsg(comp, title, msg)

        })
    } // showInfoMsgAsync()

    /**
     * Shows an information message.
     *
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     */
    def showInfoMsg(title: String, msg: Object): Unit = showInfoMsg(null, title, msg)

    /**
     * Shows an information message asynchronously.
     *
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     */
    def showInfoMsgAsync(title: String, msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showInfoMsg(title, msg)

        })
    } // showInfoMsgAsync()

    /**
     * Shows an information message.
     *
     * @param comp
     *            the root component.
     * @param msg
     *            the message.
     */
    def showInfoMsg(comp: Component, msg: Object): Unit = showInfoMsg(comp, null, msg)

    /**
     * Shows an information message asynchronously.
     *
     * @param comp
     *            the root component.
     * @param msg
     *            the message.
     */
    def showInfoMsgAsync(comp: Component, msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showInfoMsg(comp, msg)

        })
    } // showInfoMsgAsync()

    /**
     * Shows an information message.
     *
     * @param msg
     *            the message.
     */
    def showInfoMsg(msg: Object): Unit = showInfoMsg(null, null, msg)

    /**
     * Shows an information message asynchronously.
     *
     * @param msg
     *            the message.
     */
    def showInfoMsgAsync(msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showInfoMsg(msg)

        })
    } // showInfoMsgAsync()

    /**
     * Shows a huge information message. The dialog size will be hardcoded with
     * {@code width} and {@code height}.
     *
     * @param comp
     *            the root component.
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     * @param width
     *            the dialog width.
     * @param height
     *            the dialog height.
     */
    def showHugeInfoMsg(comp: Component, title: String, msg: String, width: Int,
                        height: Int) = {
        var scrollPane = new JScrollPane(new JLabel(msg))
        if (width > 0 && height > 0) {
            var size = new Dimension(width, height)
            scrollPane.setMaximumSize(size)
            scrollPane.setMinimumSize(size)
            scrollPane.setPreferredSize(size)
        }

        showInfoMsg(comp, title, scrollPane)
    } // showHugeInfoMsg()

    /**
     * Shows a huge information message asynchronously. The dialog size will be
     * hardcoded with {@code width} and {@code height}.
     *
     * @param comp
     *            the root component.
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     * @param width
     *            the dialog width.
     * @param height
     *            the dialog height.
     */
    def showHugeInfoMsgAsync(comp: Component, title: String, msg: String,
                             width: Int, height: Int) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showHugeInfoMsg(comp, title, msg, width, height)

        })
    } // showHugeInfoMsgAsync()

    /**
     * Shows a huge information message. The dialog size will be hardcoded with
     * {@code width} and {@code height}.
     *
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     * @param width
     *            the dialog width.
     * @param height
     *            the dialog height.
     */
    def showHugeInfoMsg(title: String, msg: String, width: Int, height: Int): Unit =
        showHugeInfoMsg(null, title, msg, width, height)

    /**
     * Shows a huge information message asynchronously. The dialog size will be
     * hardcoded with {@code width} and {@code height}.
     *
     * @param title
     *            the title. If {@code null}, default will be used.
     * @param msg
     *            the message.
     * @param width
     *            the dialog width.
     * @param height
     *            the dialog height.
     */
    def showHugeInfoMsgAsync(title: String, msg: String, width: Int,
                             height: Int) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showHugeInfoMsg(title, msg, width, height)

        })
    } // showHugeInfoMsgAsync()

    /**
     * Shows a huge information message. The dialog size will be hardcoded with
     * {@code width} and {@code height}.
     *
     * @param comp
     *            the root component.
     * @param msg
     *            the message.
     * @param width
     *            the dialog width.
     * @param height
     *            the dialog height.
     */
    def showHugeInfoMsg(comp: Component, msg: String, width: Int, height: Int): Unit =
        showHugeInfoMsg(comp, null, msg, width, height)

    /**
     * Shows a huge information message asynchronously. The dialog size will be
     * hardcoded with {@code width} and {@code height}.
     *
     * @param comp
     *            the root component.
     * @param msg
     *            the message.
     * @param width
     *            the dialog width.
     * @param height
     *            the dialog height.
     */
    def showHugeInfoMsgAsync(comp: Component, msg: String, width: Int,
                             height: Int) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showHugeInfoMsg(comp, msg, width, height)

        })
    } // showHugeInfoMsgAsync()

    /**
     * Shows a huge information message. The dialog size will be hardcoded with
     * {@code width} and {@code height}.
     *
     * @param msg
     *            the message.
     * @param width
     *            the dialog width.
     * @param height
     *            the dialog height.
     */
    def showHugeInfoMsg(msg: String, width: Int, height: Int): Unit =
        showHugeInfoMsg(null, null, msg, width, height)

    /**
     * Shows a huge information message asynchronously. The dialog size will be
     * hardcoded with {@code width} and {@code height}.
     *
     * @param msg
     *            the message.
     * @param width
     *            the dialog width.
     * @param height
     *            the dialog height.
     */
    def showHugeInfoMsgAsync(msg: String, width: Int, height: Int) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showHugeInfoMsg(msg, width, height)

        })
    } // showHugeInfoMsgAsync()

    /**
     * Shows a warning message.
     *
     * @param comp
     *            the root component.
     * @param title
     *            the title.
     * @param msg
     *            the message.
     */
    def showWarningMsg(comp: Component, title: String, msg: Object) =
        JOptionPane.showMessageDialog(
            comp, msg,
            if (Texts.isEmpty(title)) Messages.getString(R.string.warning)
            else title,
            JOptionPane.WARNING_MESSAGE)

    /**
     * Shows a warning message asynchronously.
     *
     * @param comp
     *            the root component.
     * @param title
     *            the title.
     * @param msg
     *            the message.
     */
    def showWarningMsgAsync(comp: Component, title: String, msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showWarningMsg(comp, title, msg)

        })
    } // showWarningMsgAsync()

    /**
     * Shows a warning message.
     *
     * @param comp
     *            the root component.
     * @param msg
     *            the message.
     */
    def showWarningMsg(comp: Component, msg: Object): Unit = showWarningMsg(comp, null, msg)

    /**
     * Shows a warning message asynchronously.
     *
     * @param comp
     *            the root component.
     * @param msg
     *            the message.
     */
    def showWarningMsgAsync(comp: Component, msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showWarningMsg(comp, msg)

        })
    } // showWarningMsgAsync()

    /**
     * Shows a warning message.
     *
     * @param title
     *            the title.
     * @param msg
     *            the message.
     */
    def showWarningMsg(title: String, msg: Object): Unit = showWarningMsg(null, title, msg)

    /**
     * Shows a warning message asynchronously.
     *
     * @param title
     *            the title.
     * @param msg
     *            the message.
     */
    def showWarningMsgAsync(title: String, msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showWarningMsg(title, msg)

        })
    } // showWarningMsgAsync()

    /**
     * Shows a warning message.
     *
     * @param msg
     *            the message.
     */
    def showWarningMsg(msg: Object): Unit = showWarningMsg(null, null, msg)

    /**
     * Shows a warning message asynchronously.
     *
     * @param msg
     *            the message.
     */
    def showWarningMsgAsync(msg: Object) = {
        SwingUtilities.invokeLater(new Runnable() {

            override def run() = showWarningMsg(msg)

        })
    } // showWarningMsgAsync()

    /**
     * Shows a yes-no confirmation dialog.
     *
     * @param comp
     *            the root component.
     * @param title
     *            the title.
     * @param msg
     *            the message.
     * @param defaultYes
     *            {@code true} to make button "Yes" selected as default.
     *            {@code false} for button "No".
     * @return {@code true} if the user chose "Yes", otherwise {@code false}.
     */
    def confirmYesNo(comp: Component, title: String, msg: Object,
                     defaultYes: Boolean): Boolean = {
        val options = Array[Object](Messages.getString(R.string.yes),
            Messages.getString(R.string.no))
        JOptionPane.showOptionDialog(
            comp, msg,
            if (Texts.isEmpty(title)) Messages.getString(R.string.confirmation)
            else title,
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
            options, options(if (defaultYes) 0 else 1)) == 0
    } // confirmYesNo()

    /**
     * Shows a yes-no confirmation dialog.
     *
     * @param title
     *            the title.
     * @param msg
     *            the message.
     * @param defaultYes
     *            {@code true} to make button "Yes" selected as default.
     *            {@code false} for button "No".
     * @return {@code true} if the user chose "Yes", otherwise {@code false}.
     */
    def confirmYesNo(title: String, msg: Object, defaultYes: Boolean): Boolean =
        confirmYesNo(null, title, msg, defaultYes)

    /**
     * Shows a yes-no confirmation dialog.
     *
     * @param comp
     *            the root component.
     * @param msg
     *            the message.
     * @param defaultYes
     *            {@code true} to make button "Yes" selected as default.
     *            {@code false} for button "No".
     * @return {@code true} if the user chose "Yes", otherwise {@code false}.
     */
    def confirmYesNo(comp: Component, msg: Object, defaultYes: Boolean): Boolean =
        confirmYesNo(comp, null, msg, defaultYes)

    /**
     * Shows a yes-no confirmation dialog.
     *
     * @param msg
     *            the message.
     * @param defaultYes
     *            {@code true} to make button "Yes" selected as default.
     *            {@code false} for button "No".
     * @return {@code true} if the user chose "Yes", otherwise {@code false}.
     */
    def confirmYesNo(msg: Object, defaultYes: Boolean): Boolean =
        confirmYesNo(null, null, msg, defaultYes)

}
