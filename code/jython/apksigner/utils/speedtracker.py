#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

import time

class SpeedTracker:
    ''' A tracker of speed.

        **Note:** Use the speeds in your own unit (bytes/ seconds, bytes/
        nanosecond...)
    '''

    class CheckPoint:
        ''' The check point.
        '''

        def __init__(self, speed, tick=0):
            ''' Constructor.
            '''

            self.tick = tick if tick > 0 else time.time()
            self.speed = speed
            #.__init__()

        #.CheckPoint

    def __init__(self, max_checkpoints=500, max_period=10):
        ''' Constructor.
        '''

        self.max_checkpoints = max_checkpoints
        self.max_period = max_period
        self.checkpoints = []
        #.__init__()

    def add(self, speed):
        ''' Add new check point with ``speed`` at current time.
        '''

        self.checkpoints.append(self.CheckPoint(speed=speed))
        if len(self.checkpoints) > self.max_checkpoints:
            del self.checkpoints[0]
        #.add()

    def clear(self):
        ''' Clears all check points.
        '''

        del self.checkpoints[:]
        #.clear()

    def instantaneous_speed(self):
        ''' Calculates instantaneous speed.
        '''

        tick = time.time()
        total_speed = 0
        for i in range(len(self.checkpoints) - 1, -1, -1):
            cp = self.checkpoints[i]
            if tick - cp.tick <= self.max_period:
                total_speed += cp.speed
            else:
                del self.checkpoints[:i+1]
                break
            #.for

        return 0 if len(self.checkpoints) == 0 else total_speed / len(self.checkpoints)
        #.instantaneous_speed()

    #.SpeedTracker