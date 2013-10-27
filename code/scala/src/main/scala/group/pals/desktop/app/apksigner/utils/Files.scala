/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

import group.pals.desktop.app.apksigner.i18n.Messages
import group.pals.desktop.app.apksigner.i18n.R
import group.pals.desktop.app.apksigner.utils.ui.Dlg

import java.awt.event.KeyEvent
import java.io.File
import java.util.regex.Pattern

import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileFilter

/**
 * File utilities.
 *
 * @author Hai Bison
 *
 */
object Files {

    /**
     * File handling buffer (reading, writing...) -- {@code 32 KiB}.
     */
    lazy final val FILE_BUFFER = 32 * 1024

    /**
     * Removes invalid characters...
     *
     * @param name
     *            the name to fix.
     * @return the "fresh" name :-)
     */
    def fixFilename(name: String): String =
        if (name == null)
            null
        else
            name.replaceAll("[\\\\/?%*:|\"<>]+", "").trim()

    /**
     * Appends {@code suffix} to {@code fileName}, makes sure the {@code suffix}
     * is placed before the file's extension (if there is one).
     *
     * @param fileName
     *            the original file name.
     * @param suffix
     *            the suffix.
     * @return the new file name.
     */
    def appendFilename(fileName: String, suffix: String): String = {
        if (fileName.matches("(?si).+\\.[^ \t]+")) {
            val iPeriod = fileName.lastIndexOf(KeyEvent.VK_PERIOD)
            return fileName.substring(0, iPeriod) + suffix
                + (char) KeyEvent.VK_PERIOD + fileName.substring(iPeriod + 1)
        }

        fileName + suffix
    }// appendFilename()

    /**
     * Opens a dialog to choose a file.
     *
     * @param startupDir
     *            the startup directory.
     * @return the chosen file, or {@code null} if the user cancelled.
     */
    def chooseFile(startupDir File): File = {
        val fc = new JFileChooserEx(startupDir)
        fc.setDialogTitle(Messages.getString(R.string.choose_file))
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY)

        fc.showOpenDialog(null) match {
        case JFileChooser.APPROVE_OPTION => return fc.getSelectedFile()
        case _ => null
        }
    }// chooseFile()

    /**
     * Opens a dialog to choose a file.
     *
     * @param startupDir
     *            the startup directory.
     * @param regexFilenameFilter
     *            the regex filename filter.
     * @param description
     *            the file filter description.
     * @return the chosen file, can be {@code null}.
     */
    def chooseFile(startupDir File, regexFilenameFilter: String,
            description: String): File = {
        val fc = new JFileChooserEx(startupDir)
        fc.setDialogTitle(Messages.getString(R.string.choose_file))
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY)
        fc.addFilenameFilter(regexFilenameFilter, description, true)

        fc.showOpenDialog(null) match {
        case JFileChooser.APPROVE_OPTION => fc.getSelectedFile()
        case _ => null
        }
    }// chooseFile()

    /**
     * Opens a dialog to choose a directory.
     *
     * @param startupDir
     *            the startup directory.
     * @return the chosen directory, can be {@code null}.
     */
    def chooseDir(startupDir File): File = {
        val fc = new JFileChooserEx(startupDir)
        fc.setDialogTitle(Messages.getString(R.string.choose_directory))
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)

        fc.showOpenDialog(null) match {
        case JFileChooser.APPROVE_OPTION => fc.getSelectedFile()
        case _ => null
        }
    }// chooseDir()

    /**
     * Opens a dialog to choose a file to save.
     *
     * @param startupDir
     *            the startup directory.
     * @return the chosen file, can be {@code null}.
     */
    def chooseFileToSave(startupDir File) =
        chooseFileToSave(startupDir, null, null, null)

    /**
     * Opens a dialog to choose a file to save.
     *
     * @param startupDir
     *            the startup directory.
     * @param defaultFileExt
     *            the default file extension.
     * @param regexFilenameFilter
     *            the regex filename filter.
     * @param description
     *            the file filter description.
     * @return the chosen file, can be {@code null}.
     */
    def chooseFileToSave(startupDir File, defaultFileExt: String,
            regexFilenameFilter: String, description: String): File = {
        val fc = new JFileChooserEx(startupDir)
        fc.setDialogTitle(Messages.getString(R.string.save_as))
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY)
        fc.setDefaultFileExt(defaultFileExt)
        if (regexFilenameFilter != null)
            fc.addFilenameFilter(regexFilenameFilter, description, true)

        fc.showSaveDialog(null) match {
        case JFileChooser.APPROVE_OPTION => fc.getSelectedFile()
        case _ => null
        }
    }// chooseFileToSave()

    /**
     * Creates new file filter.
     *
     * @param fileSelectionMode
     *            one of {@link JFileChooser#FILES_ONLY},
     *            {@link JFileChooser#DIRECTORIES_ONLY},
     *            {@link JFileChooser#FILES_AND_DIRECTORIES}.
     * @param regex
     *            the regex string to filter filenames.
     * @param description
     *            the description.
     * @return the {@link FileFilter} object.
     */
    def newFileFilter(fileSelectionMode: Int, regex: String,
            description: String): FileFilter = {
        new FileFilter() {

            @Override
            def accept(f: File): Boolean = {
                if (fileSelectionMode == JFileChooser.DIRECTORIES_ONLY)
                    f.getName().matches(regex)
                else if (f.isDirectory())
                    true
                else
                    f.getName().matches(regex)
            }// accept()

            @Override
            def getDescription() = description
        }
    }// newFileFilter()

    /**
     * Extended class of {@link JFileChooser}, which hacks some methods :-)
     */
    class JFileChooserEx(val startupDir: File) extends
            JFileChooser(startupDir) {

        /**
         * Auto-generated by Eclipse.
         */
        lazy final val serialVersionUID = -8249130783203341207L

        private var mDefaultFileExt: String = null

        /**
         * Adds the regex file name filter.
         *
         * @param regex
         *            the regular expression.
         * @param description
         *            the description.
         * @return the {@link FileFilter}.
         */
        def addFilenameFilter(regex: String, description: String) =
            addFilenameFilter(regex, description, false)

        /**
         * Adds the regex file name filter.
         *
         * @param regex
         *            the regular expression.
         * @param description
         *            the description.
         * @param setAsMainFilter
         *            {@code true} if you want to set the main filter to this
         *            one.
         * @return the {@link FileFilter}.
         */
        def addFilenameFilter(regex: String, description: String,
                setAsMainFilter: Boolean) = {
            new FileFilter() {

                addChoosableFileFilter(this)
                if (setAsMainFilter)
                    setFileFilter(this)

                override def accept(f: File): Boolean = {
                    if (getFileSelectionMode() == DIRECTORIES_ONLY)
                        f.getName().matches(regex)
                    else if (f.isDirectory())
                        true
                    else
                        f.getName().matches(regex)
                }// accept()

                override def getDescription() = description

            }
        }// addFilenameFilter()

        /**
         * Sets default file extension in {@link #SAVE_DIALOG} mode.
         *
         * @param fileExt
         *            the default file extension to set.
         * @return the instance of this class, to allow chaining multiple calls
         *         into a single statement.
         */
        def setDefaultFileExt(fileExt: String): this.type = {
            mDefaultFileExt = fileExt
            this
        }// setDefaultFileExt()

        override def approveSelection() = {
            getDialogType() match {
            case SAVE_DIALOG => {
                if (getCurrentDirectory() == null
                        || !getCurrentDirectory().canWrite()) {
                    Dlg.showErrMsg(Messages
                            .getString(R.string.msg_cannot_save_a_file_here))
                    return
                }

                var file = getSelectedFile()
                if (file != null && mDefaultFileExt != null) {
                    if (!file.getName().matches(
                            "(?si).+" + Pattern.quote(mDefaultFileExt))) {
                        file = new File(file.getParent() + File.separator
                                + file.getName() + mDefaultFileExt)
                        setSelectedFile(file)
                    }
                }
                if ((file != null) && file.exists()) {
                    val userOptions = Array(
                            Messages.getString(R.string.yes),
                            Messages.getString(R.string.no) )
                    val usrOption = JOptionPane.showOptionDialog(this, Messages
                            .getString(R.string.pmsg_override_file,
                                    file.getName()), Messages
                            .getString(R.string.confirmation),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE, null, userOptions,
                            userOptions[1])
                    if (usrOption != 0)
                        return
                }
            }// case SAVE_DIALOG

            case OPEN_DIALOG => {
                var file = getSelectedFile()
                if (file == null || !file.exists()) {
                    Dlg.showErrMsg(Messages.getString(
                            R.string.pmsg_file_not_exist, file == null ? ""
                                    : file.getName()))
                    return
                }
            }// case OPEN_DIALOG
            }

            super.approveSelection()
        }// approveSelection()

    }// JFileChooserEx

}
