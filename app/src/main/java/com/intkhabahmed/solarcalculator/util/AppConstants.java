package com.intkhabahmed.solarcalculator.util;

public interface AppConstants {
    int MILLIS_IN_A_DAY = 86400000;
    /**
     * The sunrise and moonrise algorithms return values in a double[] vector
     * where result[RISE] is the time of sun (moon) rise and result[SET] is the
     * value of sun (moon) set. Sun.riseSetLST() also returns result[ASIMUTH_RISE]
     * and result[ASIMUTH_SET].
     */
    int RISE = 0;
    int SET = 1;
    /**
     * ABOVE_HORIZON and BELOW_HORIZON are returned for sun and moon calculations
     * where the astronomical object does not cross the horizon.
     */
    double ABOVE_HORIZON = Double.POSITIVE_INFINITY;
    double BELOW_HORIZON = Double.NEGATIVE_INFINITY;

    /**
     * Degrees -> Radians: degree * DegRad Radians -> Degrees: radians / DegRad
     */
    double DegRad = (Math.PI / 180.0);
}
