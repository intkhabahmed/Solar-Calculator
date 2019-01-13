package com.intkhabahmed.solarcalculator.util;

import static com.intkhabahmed.solarcalculator.util.AppConstants.DegRad;

public class Astro {
    /*
     * JD = MJD + Astro.epochMJD;
     */
    public static final double epochMJD = 2400000.5;
    static final int RA = 0; /* RightAsension from solarEphemeris */
    static final int DEC = 1; /* DeclinatiOn from solarEphemeris */
    /*
     * Constants for solarEphemeris.
     */
    private static final double CosEPS = 0.91748;
    private static final double SinEPS = 0.39778;
    private static final double P2 = Math.PI * 2.0;
    /*
     * Constants for lunarEphemeris
     */
    private static final double ARC = 206264.8062;

    static double cos(double value) {
        return (Math.cos(value * DegRad));
    }

    /**
     * Pascal fraction function. Rounds towards zero.
     *
     * @param value
     * @result integer (rounded towards zero)
     */
    private static double FRAC(double value) {
        double result = value - TRUNC(value);
        if (result < 0.0)
            result += 1.0;
        return (result);
    }

    static boolean isEvent(double value) {
        return (value != AppConstants.ABOVE_HORIZON && value != AppConstants.BELOW_HORIZON);
    }

    /**
     * Compute Local Mean Sidereal Time (LMST).
     *
     * @param MJD       Modified Julian Day number
     * @param longitude Longitude in degrees, East is positive.
     * @result The local mean sidereal time.
     */
    static double LMST( /* Used by Moon.java */
            double MJD, double longitude) {
        double MJD0 = Math.floor(MJD);
        double UT = (MJD - MJD0) * 24.0;
        double T = (MJD0 - 51544.5) / 36525.0;
        double GMST = 6.697374558 + 1.0027379093 * UT
                + (8640184.812866 + (0.093104 - 6.2E-6 * T) * T) * T / 3600.0;
        double LMST = 24.0 * FRAC((GMST + longitude / 15.0) / 24.0);
        return (LMST);
    }

    /**
     * This is the low-precision lunar ephemeris, MiniMoon, from Astronomy on the
     * Personal Computer, p. 38. It is accurate to about 5'.
     *
     * @return result[0] = rightAscension, result[1] = declination
     * @parameter actualMJD The modified Julian Date for the actual time to be
     * computed.
     */
    static double[] lunarEphemeris(double MJD) {
        double T = (MJD - 51544.5) / 36525.0;
        double L0 = Astro.FRAC(0.606433 + 1336.855225 * T); /*
         * Mean longitude
         * (revolutions)
         */
        double L = P2 * Astro.FRAC(0.374897 + 1325.552410 * T); /* Moon mean anomaly */
        double LS = P2 * Astro.FRAC(0.993133 + 99.997361 * T); /* Sun mean anomaly */
        double D = P2 * Astro.FRAC(0.827361 + 1236.853086 * T); /* Moon - Sun */
        double F = P2 * Astro.FRAC(0.259086 + 1342.227825 * T); /*
         * mean latitude
         * argument
         */
        double DL = 22640 * Math.sin(L) - 4586 * Math.sin(L - 2 * D) + 2370
                * Math.sin(2 * D) + 769 * Math.sin(2 * L) - 668 * Math.sin(LS) - 412
                * Math.sin(2 * F) - 212 * Math.sin(2 * L - 2 * D) - 206
                * Math.sin(L + LS - 2 * D) + 192 * Math.sin(L + 2 * D) - 165
                * Math.sin(LS - 2 * D) - 125 * Math.sin(D) - 110 * Math.sin(L + LS)
                + 148 * Math.sin(L - LS) - 55 * Math.sin(2 * F - 2 * D);
        double S = F + (DL + 412 * Math.sin(2 * F) + 541 * Math.sin(LS)) / ARC;
        double H = F - 2 * D;
        double N = -526 * Math.sin(H) + 44 * Math.sin(L + H) - 31
                * Math.sin(-L + H) - 23 * Math.sin(LS + H) + 11 * Math.sin(-LS + H)
                - 25 * Math.sin(-2 * L + F) + 21 * Math.sin(-L + F);
        double L_MOON = P2 * Astro.FRAC(L0 + DL / 1296.0E3); /* L in radians */
        double B_MOON = (18520.0 * Math.sin(S) + N) / ARC; /* B in radians */
        /* Equatorial coordinates */
        double CB = Math.cos(B_MOON);
        double X = CB * Math.cos(L_MOON);
        double V = CB * Math.sin(L_MOON);
        double W = Math.sin(B_MOON);
        double Y = CosEPS * V - SinEPS * W;
        double Z = SinEPS * V + CosEPS * W;
        double RHO = Math.sqrt(1.0 - Z * Z);
        double[] result = new double[2];
        result[DEC] = (360.0 / P2) * Math.atan(Z / RHO);
        result[RA] = (48.0 / P2) * Math.atan(Y / (X + RHO));
        if (result[RA] < 0.0) {
            result[RA] += 24.0;
        }
        return (result);
    }

    /**
     * Trignometric classes that take degree arguments.
     */
    static double sin(double value) {
        return (Math.sin(value * DegRad));
    }

    /**
     * Pascal truncate function. Returns the integer nearest to zero. (This
     * behaves differently than C/Java Math.floor() for negative values.)
     *
     * @param value The value to convert
     * @result Integral value nearest zero
     */
    private static double TRUNC(double value) {
        double result = Math.floor(Math.abs(value));
        if (value < 0.0)
            result = (-result);
        return (result);
    }
}
