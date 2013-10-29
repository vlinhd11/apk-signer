/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner

import java.awt.Color
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File

import group.pals.desktop.app.apksigner.i18n.Messages
import group.pals.desktop.app.apksigner.i18n.R
import group.pals.desktop.app.apksigner.services.BaseThread
import group.pals.desktop.app.apksigner.services.INotification
import group.pals.desktop.app.apksigner.services.Message
import group.pals.desktop.app.apksigner.services.ServiceManager
import group.pals.desktop.app.apksigner.services.Updater
import group.pals.desktop.app.apksigner.ui.prefs.DialogPreferences
import group.pals.desktop.app.apksigner.utils.Files
import group.pals.desktop.app.apksigner.utils.Preferences
import group.pals.desktop.app.apksigner.utils.Sys
import group.pals.desktop.app.apksigner.utils.Texts
import group.pals.desktop.app.apksigner.utils.ui.Dlg
import group.pals.desktop.app.apksigner.utils.ui.FileDrop
import group.pals.desktop.app.apksigner.utils.ui.JEditorPopupMenu
import group.pals.desktop.app.apksigner.utils.ui.UI
import javax.swing.AbstractAction
import javax.swing.BorderFactory
import javax.swing.ButtonGroup
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JRadioButtonMenuItem
import javax.swing.JScrollPane
import javax.swing.JTabbedPane
import javax.swing.JTextField
import javax.swing.KeyStroke
import javax.swing.SpringLayout
import javax.swing.SwingConstants
import javax.swing.WindowConstants
import javax.swing.border.TitledBorder

/**
 * Main activity.
 *
 * @author Hai Bison
 * @since v1.6 beta
 */
class MainActivity {

    /**
     * The class name.
     */
    private lazy final val CLASSNAME = classOf[MainActivity].getName()

    /**
     * This key holds the last working directory.
     */
    private lazy final val PKEY_LAST_WORKING_DIR = CLASSNAME + ".last_working_dir"

    /**
     * This key holds the last tab index.
     */
    private lazy final val PKEY_LAST_TAB_INDEX = CLASSNAME + ".last_tab_index"

    /*
     * FIELDS
     */

    private var mUpdater: Updater = null

    /*
     * CONTROLS
     */

    private var mMenuItemNotification: JMenuItem = null
    private var mMainFrame: JFrame = null
    private var mTextJdkPath: JTextField = null
    private var mTabbedPane: JTabbedPane = null
    private var mMenuLanguage: JMenu = null

    /**
     * Initialize the contents of the frame.
     */
    private def initialize(): Unit = {
        mMainFrame = new JFrame()
        mMainFrame.setBounds(100, 100, 450, 300)
        mMainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
        mMainFrame.setIconImage(Assets.iconLogo())

        var mMenuBar = new JMenuBar()
        mMainFrame.setJMenuBar(mMenuBar)

        var mMenuFile = new JMenu(Messages.getString(R.string.file)) //$NON-NLS-1$
        mMenuBar.add(mMenuFile)

        var mMenuItemPreferences = new JMenuItem(
            Messages.getString(R.string.settings)) //$NON-NLS-1$
        mMenuItemPreferences
            .addActionListener(mMenuItemPreferencesActionListener)
        mMenuFile.add(mMenuItemPreferences)

        mMenuFile.addSeparator()

        var mMenuItemExit = new JMenuItem(
            Messages.getString(R.string.exit)) //$NON-NLS-1$
        mMenuItemExit.addActionListener(mMenuItemExitActionListener)
        mMenuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
            InputEvent.CTRL_MASK))
        mMenuFile.add(mMenuItemExit)

        var mMenuHelp = new JMenu(Messages.getString(R.string.help)) //$NON-NLS-1$
        mMenuBar.add(mMenuHelp)

        var mMenuItemAbout = new JMenuItem(
            Messages.getString(R.string.about)) //$NON-NLS-1$
        mMenuItemAbout.addActionListener(mMenuItemAboutActionListener)
        mMenuHelp.add(mMenuItemAbout)

        mMenuLanguage = new JMenu(Messages.getString(R.string.language))
        mMenuBar.add(mMenuLanguage)

        mMenuItemNotification = new JMenuItem()
        mMenuBar.add(mMenuItemNotification)
        mMenuItemNotification.setForeground(Color.yellow)

        var springLayout = new SpringLayout()
        mMainFrame.getContentPane().setLayout(springLayout)

        mTextJdkPath = new JTextField()
        mTextJdkPath.setEditable(false)
        mTextJdkPath.setBorder(new TitledBorder(null, Messages
            .getString(R.string.desc_jdk_path), TitledBorder.LEADING,
            TitledBorder.TOP, null, null))
        springLayout.putConstraint(SpringLayout.NORTH, mTextJdkPath, 10,
            SpringLayout.NORTH, mMainFrame.getContentPane())
        springLayout.putConstraint(SpringLayout.WEST, mTextJdkPath, 10,
            SpringLayout.WEST, mMainFrame.getContentPane())
        mMainFrame.getContentPane().add(mTextJdkPath)

        var mBtnChooseJdkPath = new JButton(
            Messages.getString(R.string.choose_jdk_path)) //$NON-NLS-1$
        mBtnChooseJdkPath.addActionListener(mBtnChooseJdkPathActionListener)
        springLayout.putConstraint(SpringLayout.EAST, mTextJdkPath, -10,
            SpringLayout.WEST, mBtnChooseJdkPath)
        springLayout.putConstraint(SpringLayout.NORTH, mBtnChooseJdkPath, 10,
            SpringLayout.NORTH, mMainFrame.getContentPane())
        springLayout.putConstraint(SpringLayout.EAST, mBtnChooseJdkPath, -10,
            SpringLayout.EAST, mMainFrame.getContentPane())
        mMainFrame.getContentPane().add(mBtnChooseJdkPath)

        mTabbedPane = new JTabbedPane(SwingConstants.TOP)
        springLayout.putConstraint(SpringLayout.SOUTH, mBtnChooseJdkPath, -10,
            SpringLayout.NORTH, mTabbedPane)
        springLayout.putConstraint(SpringLayout.NORTH, mTabbedPane, 10,
            SpringLayout.SOUTH, mTextJdkPath)
        springLayout.putConstraint(SpringLayout.WEST, mTabbedPane, 5,
            SpringLayout.WEST, mMainFrame.getContentPane())
        springLayout.putConstraint(SpringLayout.SOUTH, mTabbedPane, -5,
            SpringLayout.SOUTH, mMainFrame.getContentPane())
        springLayout.putConstraint(SpringLayout.EAST, mTabbedPane, -5,
            SpringLayout.EAST, mMainFrame.getContentPane())
        mMainFrame.getContentPane().add(mTabbedPane)
    } // initialize()

    /**
     * Initializes tabs.
     */
    private def initTabs(): Unit = {
        mTabbedPane.add(Messages.getString(R.string.key_generator),
            new PanelKeyGen())
        mTabbedPane.add(Messages.getString(R.string.signer), new PanelSigner())
        mTabbedPane.add(Messages.getString(R.string.apk_alignment),
            new PanelApkAlignment())
        mTabbedPane.add(Messages.getString(R.string.key_tools),
            new PanelKeyTools())

        /*
         * Select the last tab index.
         */

        var lastTabIndex = 0
        try {
            lastTabIndex = Preferences.get(
                PKEY_LAST_TAB_INDEX).toInt
        } catch {
            case _: Exception =>
            /*
             * Ignore it.
             */
        }

        if (lastTabIndex >= mTabbedPane.getTabCount())
            lastTabIndex = mTabbedPane.getTabCount() - 1
        if (lastTabIndex < 0)
            lastTabIndex = 0

        mTabbedPane.setSelectedIndex(lastTabIndex)
    } // initTabs()

    /**
     * Shows main window.
     */
    def show(): Unit = mMainFrame.setVisible(true)

    /**
     * Gets the JDK path.
     *
     * @return the JDK path.
     */
    private def jdkPath: File = Preferences.jdkPath

    /**
     * Sets the JDK path.
     *
     * @param file
     *            the JDK path to set.
     */
    private def jdkPath_=(file: File) = {
        if (file != null && file.isDirectory() && file.canRead()) {
            Preferences.set(PKEY_LAST_WORKING_DIR,
                file.getParentFile().getAbsolutePath())
            Preferences.jdkPath = file
            mTextJdkPath.setText(file.getAbsolutePath())
        } else {
            Preferences.jdkPath = null
            mTextJdkPath.setText(null)
        }
    } // jdkPath()

    /*
     * LISTENERS
     */

    private lazy final val mMainFrameWindowAdapter = new WindowAdapter() {

        override def windowClosing(e: WindowEvent): Unit = {
            Preferences.set(PKEY_LAST_TAB_INDEX,
                mTabbedPane.getSelectedIndex().toString())
            Preferences.store()

            val activeThreads = ServiceManager
                .activeThreads()
            if (activeThreads.isEmpty) {
                mMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
            } else {
                var serviceNames = new StringBuilder()
                for (thread <- activeThreads)
                    serviceNames.append(" - ").append(thread.getName())
                        .append('\n')
                if (Dlg.confirmYesNo(
                    String.format(
                        "%s\n\n%s\n%s",
                        Messages.getString(
                            if (activeThreads.size > 1) R.string.pmsg_there_are_x_services_running
                            else R.string.pmsg_there_is_x_service_running,
                            activeThreads.size),
                        serviceNames,
                        Messages.getString(R.string.msg_do_you_want_to_exit)),
                    false)) {
                    for (thread <- activeThreads)
                        thread.interrupt()
                    mMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
                } // confirmYesNo()
            } // There are active threads
        } // windowClosing()

    } // mMainFrameWindowAdapter

    private lazy final val mUpdaterServiceNotification = new INotification() {

        override def onMessage(msg: Message): Boolean = {
            msg.id match {
                case BaseThread.MSG_DONE =>
                    mUpdater = null

                case _ =>
                    mMenuItemNotification.setText(msg.shortMessage)
                    mMenuItemNotification.setAction(new AbstractAction(
                        msg.shortMessage) {

                        /**
                         * Auto-generated by Eclipse.
                         */
                        private lazy final val serialVersionUID = -7002312198404671927L

                        override def actionPerformed(e: ActionEvent): Unit = {
                            if (!Texts.isEmpty(msg.detailedMessage))
                                Dlg.showInfoMsg(msg.detailedMessage)
                        } // actionPerformed()

                    })
            }

            return false
        } // onMessage()

    } // mUpdaterServiceNotification

    private lazy final val mMenuItemAboutActionListener = new ActionListener() {

        override def actionPerformed(e: ActionEvent): Unit = {
            val msg = String.format(Assets.pHtmlAbout().toString(),
                Messages.getString(R.string.pmsg_app_name, Sys.APP_NAME,
                    Sys.APP_VERSION_NAME))

            var label = new JLabel(new ImageIcon(Assets.iconSplash()),
                SwingConstants.CENTER)
            label.setVerticalAlignment(SwingConstants.TOP)
            label.setHorizontalTextPosition(SwingConstants.CENTER)
            label.setVerticalTextPosition(SwingConstants.BOTTOM)
            label.setText(msg)

            var scrollPane = new JScrollPane(label)
            /*
             * TODO hard code?
             */
            scrollPane.getVerticalScrollBar().setUnitIncrement(16)
            scrollPane.getHorizontalScrollBar().setUnitIncrement(16)
            var size = new Dimension(630, 270)
            scrollPane.setMaximumSize(size)
            scrollPane.setMinimumSize(size)
            scrollPane.setPreferredSize(size)

            Dlg.showInfoMsg(null, null, scrollPane)
        } // actionPerformed()

    } // mMenuItemAboutActionListener

    private lazy final val mMenuItemPreferencesActionListener = new ActionListener() {

        override def actionPerformed(e: ActionEvent): Unit = {
            var dialog = new DialogPreferences(mMainFrame)
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
            UI.setWindowCenterScreen(dialog, 40)
            dialog.setVisible(true)
        } // actionPerformed()

    } // mMenuItemPreferencesActionListener

    private lazy final val mMenuItemExitActionListener = new ActionListener() {

        override def actionPerformed(e: ActionEvent): Unit = {
            mMainFrame.dispatchEvent(new WindowEvent(mMainFrame,
                WindowEvent.WINDOW_CLOSING))
        } // actionPerformed()

    } // mMenuItemExitActionListener

    private lazy final val mBtnChooseJdkPathActionListener = new ActionListener() {

        override def actionPerformed(e: ActionEvent): Unit = {
            jdkPath = Files.chooseDir(new File(Preferences.get(
                PKEY_LAST_WORKING_DIR, "/")))
        } // actionPerformed()

    } // mBtnChooseJdkPathActionListener

    private lazy final val mTextJdkPathFileDropListener = new FileDrop.Listener() {

        override def onFilesDropped(files: Array[File]): Unit =
            jdkPath = files(0)

    } // mTextJdkPathFileDropListener

    /*
     * MAIN
     */

    initialize()

    UI.setWindowCenterScreen(mMainFrame, 65)
    JEditorPopupMenu.apply(mMainFrame)

    /*
     * INITIALIZE CONTROLS
     */

    mMainFrame.setTitle(Messages.getString(R.string.pmsg_app_name,
        Sys.APP_NAME, Sys.APP_VERSION_NAME))

    /*
     * LANGUAGES
     */
    var localeTag = Preferences.localeTag
    if (!Messages.AVAILABLE_LOCALES.contains(localeTag)) {
        Preferences.localeTag = Messages.DEFAULT_LOCALE
        localeTag = Messages.DEFAULT_LOCALE
    }
    var group = new ButtonGroup()
    for (tag <- Messages.AVAILABLE_LOCALES.keySet) {
        var menuItem = new JRadioButtonMenuItem(
            Messages.AVAILABLE_LOCALES.get(tag).get)
        menuItem.addActionListener(new ActionListener() {

            override def actionPerformed(e: ActionEvent): Unit = {
                Preferences.localeTag = tag
                Dlg.showInfoMsg(Messages
                    .getString(R.string.msg_restart_app_to_apply_new_language))
            } // actionPerformed()

        })

        if (localeTag.equals(tag))
            menuItem.setSelected(true)

        group.add(menuItem)

        mMenuLanguage.add(menuItem)
    } // for

    if (jdkPath != null && jdkPath.isDirectory())
        mTextJdkPath.setText(jdkPath.getAbsolutePath())
    new FileDrop(mTextJdkPath, BorderFactory.createTitledBorder(
        UI.BORDER_FILE_DROP,
        Messages.getString(R.string.desc_jdk_path),
        TitledBorder.LEADING, TitledBorder.TOP, null,
        UI.COLOUR_BORDER_FILE_DROP), mTextJdkPathFileDropListener)

    mMainFrame.addWindowListener(mMainFrameWindowAdapter)
    UI.initJTabbedPaneHeaderMouseWheelListener(mTabbedPane)

    initTabs()
    mMainFrame.pack()

    /*
     * START UPDATER SERVICE
     */

    mUpdater = new Updater();
    ServiceManager.registerThread(mUpdater)
    mUpdater.addNotification(mUpdaterServiceNotification)
    mUpdater.start()

}
