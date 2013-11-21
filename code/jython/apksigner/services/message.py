#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

class Message:
    ''' A message, it contains an ID, and can hold an arbitrary object, a short
        message and a detailed message.
    '''

    def __init__(self, _id, obj=None, short_msg=None, detailed_msg=None):
        ''' Constructor.

            Parameters:

            :_id:
                the message ID.
            :obj:
                an arbitrary object.
            :short_msg:
                a short message.
            :detailed_msg:
                a detailed message.
        '''

        self._id = _id
        self.obj = obj
        self.short_msg = short_msg
        self.detailed_msg = detailed_msg
        #.__init__()

    #.Message