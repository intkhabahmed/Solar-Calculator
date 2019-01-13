package com.intkhabahmed.solarcalculator.util;

import java.util.Date;
import java.util.TimeZone;

public class Moon {
    /*
     * Example (from United States Naval Observatory): For 1996.11.02 (San
     * Francisco), Moonset 12:33, Moonrise 23:26
     */
    private static final double MOONRISE = Astro.sin(+8.0 / 60.0);

    /**
     * Compute the times of sunrise and sunset for a specific date and location.
     *
     * @param javaDate       Java date
     * @param timezoneOffset Observer's timezone (msec East of Greenwich)
     * @param latitude       Observer's latitude (North is positive).
     * @param longitude      Observer's longitude (East is positive).
     * @return double[] double[RISE] is the local civil time of sunrise
     * double[SET] is the local civil time of sunset Note the following
     * special result values: ABOVE_HORIZON The moon does not set
     * BELOW_HORIZON The moon does not rise Note that these are
     * independent: there are days each month when the moon rises and does
     * not set (and vice-versa).
     */
    public static double[] riseSet(Date javaDate, long timezoneOffset,
                                   double latitude, double longitude) {
        double DATE = midnightMJD(javaDate);
        double ZONE = ((double) timezoneOffset) / 86400000.0;
        DATE -= ZONE;
        double sinLatitude = Astro.sin(latitude);
        double cosLatitude = Astro.cos(latitude);
        double yMinus = sinAltitude(DATE, 0.0, longitude, cosLatitude, sinLatitude)
                - MOONRISE;
        boolean aboveHorizon = (yMinus > 0.0);
        double rise = AppConstants.BELOW_HORIZON;
        double set = AppConstants.BELOW_HORIZON;

        if (aboveHorizon) {
            rise = AppConstants.ABOVE_HORIZON;
            set = AppConstants.ABOVE_HORIZON;
        }
        for (double hour = 1.0; hour <= 24.0; hour += 2.0) {
            double yThis = sinAltitude(DATE, hour, longitude, cosLatitude,
                    sinLatitude)
                    - MOONRISE;
            double yPlus = sinAltitude(DATE, hour + 1.0, longitude, cosLatitude,
                    sinLatitude)
                    - MOONRISE;
            /*
             * .________________________________________________________________. |
             * Quadratic interpolation through the three points: | | [-1, yMinus], [0,
             * yThis], [+1, yNext] | | (These must not lie on a straight line.) | |
             * Note: I've in-lined this as it returns several values. |
             * .________________________________________________________________.
             */
            double root1 = 0.0;
            double root2 = 0.0;
            int nRoots = 0;
            double A = (0.5 * (yMinus + yPlus)) - yThis;
            double B = (0.5 * (yPlus - yMinus));
            double C = yThis;
            double xExtreme = -B / (2.0 * A);
            double yExtreme = (A * xExtreme + B) * xExtreme + C;
            double discriminant = (B * B) - 4.0 * A * C;
            if (discriminant >= 0.0) { /* Intersects x-axis? */
                double DX = 0.5 * Math.sqrt(discriminant) / Math.abs(A);
                root1 = xExtreme - DX;
                root2 = xExtreme + DX;
                if (Math.abs(root1) <= +1.0)
                    nRoots++;
                if (Math.abs(root2) <= +1.0)
                    nRoots++;
                if (root1 < -1.0)
                    root1 = root2;
            }
            /*
             * .________________________________________________________________. |
             * Quadratic interpolation result: | | nRoots Number of roots found (0, 1,
             * or 2) | | If nRoots == zero, there is no event in this range. | | root1
             * First root (nRoots >= 1) | | root2 Second root (nRoots == 2) | | yMinus
             * Y-value at interpolation start. If < 0, root1 is | | a moonrise event. | |
             * yExtreme Maximum value of y (nRoots == 2) -- this determines | |
             * whether a 2-root event is a rise-set or a set-rise. |
             * .________________________________________________________________.
             */
            switch (nRoots) {
                case 0: /* No root at this hour */
                    break;
                case 1: /* Found either a rise or a set */
                    if (yMinus < 0.0) {
                        rise = hour + root1;
                    } else {
                        set = hour + root1;
                    }
                    break;
                case 2: /* Found both a rise and a set */
                    if (yExtreme < 0.0) {
                        rise = hour + root2;
                        set = hour + root1;
                    } else {
                        rise = hour + root1;
                        set = hour + root2;
                    }
                    break;
            }
            yMinus = yPlus;
            if (Astro.isEvent(rise) && Astro.isEvent(set))
                break;
        }
        double result[] = new double[2];
        result[AppConstants.RISE] = rise;
        result[AppConstants.SET] = set;
        return (result);
    }

    /**
     * Compute the sine of the altitude of the object for this date, hour, and
     * location. cosLatitude and sinLatitude pre-compute the observer's location.
     *
     * @param MJD0        Modified Julian Date at midnight
     * @param hour        Hour past midnight.
     * @param longitude   Observer's longitude, East is positive.
     * @param cosLatitude Cosine(observer's latitude)
     * @param sinLatitude Sine(observer's latitude)
     * @result The sine of the object's altitude above the horizon.
     * <p>
     * Note: this overrides Sun.sinAltitude and contains the moon orbital
     * computation.
     */
    private static double sinAltitude(double MJD0, double hour,
                                      double longitude, double cosLatitude, double sinLatitude) {
        double MJD = MJD0 + (hour / 24.0);
        double[] moon = Astro.lunarEphemeris(MJD);
        double TAU = 15.0 * (Astro.LMST(MJD, longitude) - moon[Astro.RA]);
        return (sinLatitude * Astro.sin(moon[Astro.DEC]) + cosLatitude
                * Astro.cos(moon[Astro.DEC]) * Astro.cos(TAU));
    }

    private static double midnightMJD(Date date1) {
        return Math.floor(MJD(date1));
    }

    private static double MJD(Date date1) {
        long l = date1.getTime() + getSystemTimezone();
        double d = (double) l / 86400000D;
        d += 40587D;
        return d;
    }

    private static long getSystemTimezone() {
        long l;
        int i = -(new Date()).getTimezoneOffset();
        l = i * 60000;
        return l;
    }

    public static long getTimeZoneOffset(Date javaDate) {
        return (getTimeZoneOffset(javaDate, TimeZone.getDefault()));
    }

    private static long getTimeZoneOffset(Date javaDate, TimeZone timeZone) {
        long timezoneOffset = 0;
        try {
            timezoneOffset = timeZone.getRawOffset();
            if (timeZone.inDaylightTime(javaDate)) {
                timezoneOffset += 3600000;
            }
        } catch (Exception e) {
            System.err.println("Can't get System timezone: " + e);
        }
        return (timezoneOffset);
    }

}
