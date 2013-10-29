/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils.ui

import java.awt.Color
import java.awt.Rectangle
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener

import javax.swing.BorderFactory
import javax.swing.JTabbedPane

/**
 * UI utilities.
 *
 * @author Hai Bison
 * @since v1.6 beta
 */
object UI {

    /**
     * The colour of selected file.
     */
    lazy final val COLOUR_SELECTED_FILE = Color.yellow

    /**
     * The colour of waiting command.
     */
    lazy final val COLOUR_WAITING_CMD = Color.cyan

    /**
     * Default colour for border of {@link FileDrop}.
     */
    lazy final val COLOUR_BORDER_FILE_DROP = Color.yellow

    /**
     * Default border for {@link FileDrop}.
     */
    lazy final val BORDER_FILE_DROP = BorderFactory
        .createMatteBorder(1, 2, 1, 2, COLOUR_BORDER_FILE_DROP)

    /**
     * Default tab size for text component.
     */
    lazy final val TEXT_COMPONENT_TAB_SIZE = 4

    /**
     * Delay time between updates of UI, in milliseconds.
     */
    lazy final val DELAY_TIME_UPDATING_UI = 499

    /**
     * Moves the {@code window} to center of the screen, also resizes it by
     * ratio 16:9 :-) (in a multiplication with {@code luckyNo}).
     *
     * @param window
     *            the window.
     * @param luckyNo
     *            your lucky number :-)
     */
    def setWindowCenterScreen(window: java.awt.Window, luckyNo: Int): Unit =
        setWindowCenterScreen(window, luckyNo * 16, luckyNo * 9)

    /**
     * Moves the {@code window} to center of the screen, also resizes it by
     * {@code width} x {@code height}.
     *
     * @param window
     *            the window.
     * @param width
     *            the window width.
     * @param height
     *            the window height.
     */
    def setWindowCenterScreen(window: java.awt.Window, width: Int,
                              height: Int): Unit = {
        var dim = new java.awt.Dimension(width, height)
        window.setMinimumSize(dim)
        window.setSize(dim)

        setWindowCenterScreen(window)
    } // setWindowCenterScreen()

    /**
     * Moves the {@code window} to center of the screen, also resizes it by
     * {@code width} x {@code height}.
     *
     * @param window
     *            the window.
     * @param width
     *            the window width.
     * @param height
     *            the window height.
     */
    def setWindowCenterScreen(window: java.awt.Window) = {
        var dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize()
        window.setLocation(
            (dim.width - window.getWidth()) / 2,
            (dim.height - window.getHeight()) / 2)
    } // setWindowCenterScreen()

    /**
     * Initializes a tabbed pane to listen to mouse wheel events, and switch
     * tabs based on those events.
     *
     * @param tabbedPane
     *            the tabbed pane.
     */
    def initJTabbedPaneHeaderMouseWheelListener(tabbedPane: JTabbedPane): Unit = {
        tabbedPane.addMouseWheelListener(new MouseWheelListener() {

            override def mouseWheelMoved(e: MouseWheelEvent): Unit = {
                val selectedComp = tabbedPane.getSelectedComponent()
                if (selectedComp == null) return

                val headerHeight = tabbedPane.getHeight() - selectedComp.getHeight()
                if (!new Rectangle(0, 0, tabbedPane.getWidth(), headerHeight)
                    .contains(e.getPoint()))
                    return

                val tabIndex = tabbedPane.getSelectedIndex()
                val wheelRotation = e.getWheelRotation()
                if (wheelRotation > 0) {
                    if (tabIndex < tabbedPane.getTabCount() - 1)
                        tabbedPane.setSelectedIndex(tabIndex + 1)
                } else if (wheelRotation < 0) {
                    if (tabIndex > 0)
                        tabbedPane.setSelectedIndex(tabIndex - 1)
                }
            } // mouseWheelMoved()

        })
    } // initJTabbedPaneHeaderMouseWheelListener()

}
