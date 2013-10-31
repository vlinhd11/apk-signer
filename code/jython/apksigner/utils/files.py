#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

import re

from apksigner.i18n import messages, string
from apksigner.utils.ui import dlg

import java.awt.event.KeyEvent
import java.io.File
import java.util.regex.Pattern

import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileFilter


'''
File handling buffer (reading, writing...) -- ``32 KiB``.
'''
FILE_BUFFER = 32 * 1024

/**
    * Removes invalid characters...
    *
    * @param name
    *            the name to fix.
    * @return the "fresh" name :-)
    */
def fix_filename(name):
    ''' Removes invalid characters...

        Parameters:

        :name (String):
            the name to fix.
    '''
    if name: return re.sub(r'[\\\\/?%*:|"<>]+', '', name).strip()
    #.fix_filename()

def append_filename(name, suffix):
    ''' Appends ``suffix`` to ``name``, makes sure the ``suffix`` is placed
        before the file's extension (if there is one).
    '''
    if re.match(r'(?si).+\.[^ \t]+', name):
        i = name.rfind('.')
        return name[:i] + suffix + '.' + name[iPeriod + 1:]

    return name + suffix
    #.append_filename()

class JFileChooserEx(JFileChooser):
    ''' Extended class of ``JFileChooser``, which hacks some methods  :-)
    '''

    default_file_ext = None

    /**
        * Creates new instance.
        *
        * @param startup_dir
        *            the startup directory.
        */
    def __init__(startup_dir=None):
        ''' Creates new instance.

            Parameters:

            :startup_dir (File):
                the startup directory.
        '''
        super(JFileChooserEx, self).__init__(startup_dir)
        #.__init__()

    def add_filename_filter(regex, description):
        ''' Adds the regex file name filter.

            Parameters:

            :regex (String):
                the regular expression.

            :description:
                the description.

            Returns:
                new ``FileFilter``.
        '''
        return add_filename_filter(regex, description, False)
        #.add_filename_filter()

    def add_filename_filter(regex, description, set_as_main_filter):
        ''' Adds the regex file name filter.

            Parameters:

            :regex (String):
                the regular expression.

            :description:
                the description.

            :set_as_main_filter (Boolean):
                ``True`` if you want to set the main filter to this one.

            Returns:
                new ``FileFilter``.
        '''
        class _(FileFilter):

            def accept(self, f):
                if getFileSelectionMode() == DIRECTORIES_ONLY:
                    return re.match(regex, f.getName())
                elif f.isDirectory():
                    return True
                else
                    return re.match(regex, f.getName())
                #.accept()

            def getDescription(self): return description

            #._

        _ = _()
        addChoosableFileFilter(_)
        if set_as_main_filter: setFileFilter(_)
        return _
        #.add_filename_filter()

    def set_default_file_ext(ext):
        ''' Sets default file extension in ``SAVE_DIALOG`` mode.

            Returns:
                the instance of this class, for chaining multiple calls into a
                single statement.
        '''
        self.default_file_ext = ext
        return self
        #.set_default_file_ext()

    def approveSelection(self):
        ''' Overridden method.
        '''

        if getDialogType() == self.SAVE_DIALOG:
            if not getCurrentDirectory() \
                or not getCurrentDirectory().canWrite():
                dlg.show_err(
                    messages.get_string(string.msg_cannot_save_a_file_here))
                return

            f = getSelectedFile()
            if f and self.default_file_ext:
                if not re.match(
                    r'(?si).+' + re.escape(self.default_file_ext), f.getName()):
                    f = File(f.getParent() + File.separator + f.getName() \
                             + self.default_file_ext)
                    setSelectedFile(f)

            if f and f.exists():
                userOptions = [ messages.get_string(R.string.yes),
                                messages.get_string(R.string.no) ]
                opt = JOptionPane.showOptionDialog(
                    self,
                    messages.get_string(string.pmsg_override_file, f.getName()),
                    messages.get_string(string.confirmation),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    None, userOptions, userOptions[1])
                if opt != 0: return
            #.SAVE_DIALOG
        elif getDialogType() == self.OPEN_DIALOG:
            f = getSelectedFile()
            if not f or not f.exists():
                dlg.show_err(messages.get_string(
                    string.pmsg_file_not_exist,
                    f.getName() if f else ''))
                return

        JFileChooser.approveSelection(self)
        #.approveSelection()

    #.JFileChooserEx

def choose_file(startup_dir=None, regex_filename_filter=None, description=None):
    ''' Opens a dialog to choose a file.

        Parameters:

        :startup_dir (File):
            the startup directory.

        :regex_filename_filter (String):
            the regular expression to filter filenames.

        :description (String);
            the description of file filter.

        Returns:
            the chosen file, or ``None``.
    '''
    fc = JFileChooserEx(startup_dir)
    fc.setDialogTitle(messages.get_string(string.choose_file))
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY)
    if regex_filename_filter:
        fc.add_filename_filter(regex_filename_filter, description, true)

    if fc.showOpenDialog(None) == JFileChooser.APPROVE_OPTION:
        return fc.getSelectedFile()
    #.choose_file()

def choose_file_to_save(startup_dir=None, default_file_ext=None,
                        regex_filename_filter=None, description=None):
    ''' Opens a dialog to choose a file to save.

        Parameters:

        :startup_dir (File):
            the startup directory.

        :default_file_ext (String):
            the default file extension.

        :regex_filename_filter (String):
            the regular expression to filter filenames.

        :description (String):
            the file filter's description.

        Returns:
            the selected file, or ``None``.
    '''

    fc = JFileChooserEx(startup_dir)
    fc.setDialogTitle(messages.get_string(string.save_as))
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY)
    if default_file_ext:
        fc.set_default_file_ext(default_file_ext)
    if regex_filename_filter:
        fc.addFilenameFilter(regex_filename_filter, description, True)

    if fc.showSaveDialog(None) == JFileChooser.APPROVE_OPTION:
        return fc.getSelectedFile()
    #.choose_file_to_save()

def new_file_filter(file_selection_mode, regex, description):
    ''' Creates new file filter.

        Parameters:

        :file_selection_mode (int):
            one of ``JFileChooser.FILES_ONLY``,
            ``JFileChooser.DIRECTORIES_ONLY``,
            ``JFileChooser.FILES_AND_DIRECTORIES``.

        :regex (String):
            the regular expression to filter filenames.

        :description (String):
            the filter's description.
    '''

    class _(FileFilter):

        def accept(f):
            if file_selection_mode == JFileChooser.DIRECTORIES_ONLY:
                return re.match(regex, f.getName())
            elif f.isDirectory():
                return True
            return re.match(regex, f.getName())
            #.accept()

        def getDescription(): return description

        #._

    return _()
    #.new_file_filter()