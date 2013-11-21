#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

'''
Utilities for dialog boxes.
'''

import java

from apksigner.i18n import messages, string

from java.awt import Component, Dimension

from javax.swing import JLabel, JOptionPane, JScrollPane, SwingUtilities

def show_err(msg, comp=None, title=None):
    ''' Shows an error message.

        Parameters:

        :msg (Object):
            the message.

        :comp (Component):
            the root component.

        :title (String):
            the title. If ``None``, default will be used.
    '''
    JOptionPane.showMessageDialog(
        comp, msg,
        title if title else messages.get_string(string.error),
        JOptionPane.ERROR_MESSAGE)
    #.show_err()

def show_err_async(msg, comp=None, title=None):
    ''' Shows an error message asynchronously.

        Parameters:

        :msg (Object):
            the message.

        :comp (Component):
            the root component.

        :title (String):
            the title. If ``None``, default will be used.
    '''
    class _(java.lang.Runnable):
        def run(self): show_err(msg, comp, title)
    SwingUtilities.invokeLater(_())
    #.show_err_async()

def show_exception(e, comp=None, title=None):
    ''' Shows an exception message.

        Parameters:

        :e (java.lang.Exception):
            the exception.

        :comp (Component):
            the root component.

        :title (String):
            the title, if ``None``, default will be used.
    '''
    msg = messages.get_string(string.pmsg_exception,
                              e.getClass().getName(),
                              e.getMessage())
    show_err(msg, comp, title)
    #.show_exception()

/**
    * Shows an exception message asynchronously.
    *
    * @param comp
    *            the root component.
    * @param title
    *            the title. If ``None``, default will be used.
    * @param e
    *            the exception.
    */
def show_exception_async(e, comp=None, title=None):
    ''' Shows an exception message asynchronously.

        Parameters:

        :e (java.lang.Exception):
            the exception.

        :comp (Component):
            the root component.

        :title (String):
            the title, if ``None``, default will be used.
    '''
    class _(java.lang.Runnable):
        def run(self): show_exception(e, comp, title)
    SwingUtilities.invokeLater(_())
    #.show_exception_async()

def show_info(msg, comp=None, title=None):
    ''' Shows an information message.

        Parameters:

        :msg (String):
            the message.

        :comp (Component):
            the root component.

        :title (String):
            the title, if ``None``, default will be used.
    '''
    JOptionPane.showMessageDialog(
            comp,
            msg,
            title if title else messages.get_string(string.information),
            JOptionPane.INFORMATION_MESSAGE)
    #.show_info()

/**
    * Shows an information message asynchronously.
    *
    * @param comp
    *            the root component.
    * @param title
    *            the title. If ``None``, default will be used.
    * @param msg
    *            the message.
    */
def show_info_async(msg, comp=None, title=None):
    ''' Shows an information message asynchronously.

        Parameters:

        :msg (String):
            the message.

        :comp (Component):
            the root component.

        :title (String):
            the title, if ``None``, default will be used.
    '''
    class _(java.lang.Runnable):
        def run(self): show_info(msg, comp, title)
    SwingUtilities.invokeLater(_())
    #.show_info_async()

def show_huge_info(msg, width, height, comp=None, title=None):
    ''' Shows a huge information message. The dialog size will be hardcoded with
        ``width`` and ``height``.

        Parameters:

        :msg (String):
            the message.

        :width (int):
            the width.

        :height (int):
            the height.

        :comp (Component):
            the root component.

        :title (String):
            the title, if ``None``, default will be used.
    '''
    scrollPane = JScrollPane(JLabel(msg))
    if width > 0 and height > 0:
        size = Dimension(width, height)
        scrollPane.setMaximumSize(size)
        scrollPane.setMinimumSize(size)
        scrollPane.setPreferredSize(size)

    show_info(scrollPane, comp, title)
    #.show_huge_info()

def show_huge_info_async(msg, width, height, comp=None, title=None):
    ''' Shows a huge information message asynchronously. The dialog size will be
        hardcoded with ``width`` and ``height``.

        Parameters:

        :msg (String):
            the message.

        :width (int):
            the width.

        :height (int):
            the height.

        :comp (Component):
            the root component.

        :title (String):
            the title, if ``None``, default will be used.
    '''
    class _(java.lang.Runnable):
        def run(self): show_huge_info(msg, width, height, comp, title)
    SwingUtilities.invokeLater(_())
    #.show_huge_info_async()

def show_warning(msg, comp=None, title=None):
    ''' Shows a warning message.

        Parameters:

        :msg (Object):
            the message.

        :comp (Component):
            the root component.

        :title (String):
            the title, if ``None``, default will be used.
    '''
    JOptionPane.showMessageDialog(
        comp, msg,
        title if title else messages.get_string(string.warning),
        JOptionPane.WARNING_MESSAGE)
    #.show_warning()

def show_warning_async(msg, comp=None, title=None):
    ''' Shows a warning message asynchronously.

        Parameters:

        :msg (Object):
            the message.

        :comp (Component):
            the root component.

        :title (String):
            the title, if ``None``, default will be used.
    '''
    class _(java.lang.Runnable):
        def run(self): show_warning(msg, comp, title)
    SwingUtilities.invokeLater(_())
    #.show_warning_async()

def confirm_yes_no(msg, default_yes, comp=None, title=None):
    ''' Shows a yes-no confirmation dialog.

        Parameters:

        :msg (Object):
            the message.

        :default_yes (Boolean):
            ``True`` to make button 'Yes' selected as default, ``None`` for
            button 'No'.

        :comp (Component):
            the root component.

        :title (String):
            the title, if ``None``, default will be used.
    '''
    options = [ messages.get_string(string.yes),
                messages.get_string(string.no) ]
    opt = JOptionPane.showOptionDialog(
            comp,
            msg,
            title if title else messages.get_string(string.confirmation),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE, None,
            options,
            options[0 if default_yes else 1])
    return opt == 0
    #.confirm_yes_no()