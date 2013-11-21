#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

'''
This is the manager for background services.
'''

from collections import OrderedDict

from basethread import BaseThread

''' Map of ``BaseThread``'s to their listeners.
'''
_THREADS = OrderedDict()

def register_thread(thread):
    ''' Registers new thread.
    '''

    def _(msg):
        if msg._id == BaseThread.MSG_DONE:
            del THREADS[thread]

    thread.listeners.append(_)
    THREADS[thread] = _
    #.register_thread()

def unregister_thread(thread):
    ''' Unregisters a thread.
    '''

    _ = THREADS.get(thread)
    if _:
        if _ in thread.listeners:
            thread.listeners.remove(_)
        del THREADS[thread]
    #.unregister_thread()

def get_active_threads():
    ''' Gets the *snapshot* list of active threads. The orders of threads are
        kept as-is like when they were registered.
    '''

    return THREADS.keys()
    #.get_active_threads()
