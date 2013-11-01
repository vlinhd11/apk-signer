#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

import threading

import message

class BaseThread(threading.Thread):
    ''' The base thread.

        Fields:

        :listeners:
            a list of functions, they have one parameter which is
            ``message.Message``. If called, they can return ``True`` to notify
            this thread that they handled the message themselves. Return
            ``None`` to let this thread continue its job.
    '''

    MSG_DONE = 0
    MSG_INFO = -1
    MSG_WARNING = -2
    MSG_ERROR = -3

    def __init__(self, name=None):
        ''' Constructor.

            Parameters:

            :name:
                the thread name.
        '''

        super(BaseThread, self).__init__()

        self.name = name
        self.listeners = []
        #.__init__()

    def send_notification(msg):
        ''' Sends notification to all listeners.

            Parameters:

            :msg:
                can be an ``int`` (which will be used for ``Message._id``) or a
                ``Message``.

            Returns:
                ``True`` if any of the listeners handled the message, ``None``
                otherwise.
        '''

        if not self.listeners: return

        if type(msg) == int:
            msg = message.Message(msg)

        for l in listeners:
            if l(msg): return True
        #.send_notification()

    #.BaseThread