/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

/**
 * A tracker of speed.
 * <p>
 * <b>Notes:</b> Use the speeds in your own unit (bytes/ seconds, bytes/
 * nanosecond...)
 * </p>
 *
 * @param maxCheckpoints
 *            Max check-points to be stored.
 * @param maxPeriod
 *            Max period time for calculating speed, in nanoseconds.
 * @author Hai Bison
 * @since v1.6 beta
 */
class SpeedTracker(maxCheckpoints: Int = 500, maxPeriod: Double = 5e9) {

    // private static final String CLASSNAME = SpeedTracker.class.getName()

    /**
     * The check-point.
     *
     * @author Hai Bison
     * @since v1.6 beta
     */
    class CheckPoint(val tick: Double = System.nanoTime(), val speed: Double) {}

    private final val mCheckPoints = List[CheckPoint]()

    /**
     * Adds new instantaneous speed.
     *
     * @param speed
     *            the instantaneous speed.
     */
    def +(speed: Double) = {
        mCheckPoints :+= new CheckPoint(speed=speed)
        if (mCheckPoints.size > maxCheckpoints)
            mCheckPoints = mCheckPoints.drop(1)
    }// +()

    /**
     * Clears all data.
     */
    def clear() = mCheckPoints = Nil

    /**
     * Calculates current instantaneous speed, also removes all of old data (old
     * check-points).
     *
     * @return the current instantaneous speed.
     */
    def calcInstantaneousSpeed(): Double = synchronized {
        var totalSpeed = 0d

        def res() =
            if (mCheckPoints.isEmpty) 0 else totalSpeed / mCheckPoints.size

        try {
            val tick = System.nanoTime()
            for (i <- mCheckPoints.size - 1 to 0 by -1) {
                var cp = mCheckPoints(i)
                if (tick - cp.tick <= maxPeriod)
                    totalSpeed += cp.speed
                else {
                    mCheckPoints = mCheckPoints.drop(i + 1)
                    return res()
                }
            }

            res()
        } catch {
            case _: Throwable => 0
        }
    }// calcInstantaneousSpeed()

}
