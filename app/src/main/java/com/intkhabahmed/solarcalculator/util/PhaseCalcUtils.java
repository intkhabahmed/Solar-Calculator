package com.intkhabahmed.solarcalculator.util;

public class PhaseCalcUtils {
    public static int getDayOfYear(int day, int month, int year) {
        int n1 = (int) Math.floor(275 * month / 9);
        int n2 = (int) Math.floor((month + 9) / 12);
        int n3 = (int) (1 + Math.floor((year - 4 * Math.floor(year / 4) + 2) / 3));
        return n1 - (n2 * n3) + day - 30;
    }

    public static long calcSunriseAndSunset(int day, boolean sunrise, double latitude, double longitude) {
        double D2R = Math.PI / 180;
        double R2D = 180 / Math.PI;
        double zenith = 90.83333333333333;

        // convert the longitude to hour value and calculate an approximate time
        double lnHour = longitude / 15;
        double approxTime;
        if (sunrise) {
            approxTime = day + ((6 - lnHour) / 24);
        } else {
            approxTime = day + ((18 - lnHour) / 24);
        }

        //calculate the Sun's mean anomaly
        double meanAnomaly = (0.9856 * approxTime) - 3.289;

        //calculate the Sun's true longitude
        double trueLongitude = meanAnomaly + (1.916 * Math.sin(meanAnomaly * D2R)) + (0.020 * Math.sin(2 * meanAnomaly * D2R)) + 282.634;
        if (trueLongitude > 360) {
            trueLongitude = trueLongitude - 360;
        } else if (trueLongitude < 0) {
            trueLongitude = trueLongitude + 360;
        }

        //calculate the Sun's right ascension
        double RA = R2D * Math.atan(0.91764 * Math.tan(trueLongitude * D2R));
        if (RA > 360) {
            RA = RA - 360;
        } else if (RA < 0) {
            RA = RA + 360;
        }

        //right ascension value needs to be in the same qua
        double Lquadrant = (Math.floor(trueLongitude / (90))) * 90;
        double RAquadrant = (Math.floor(RA / 90)) * 90;
        RA = RA + (Lquadrant - RAquadrant);

        //right ascension value needs to be converted into hours
        RA = RA / 15;

        //calculate the Sun's declination
        double sinDec = 0.39782 * Math.sin(trueLongitude * D2R);
        double cosDec = Math.cos(Math.asin(sinDec));

        //calculate the Sun's local hour angle
        double cosH = (Math.cos(zenith * D2R) - (sinDec * Math.sin(latitude * D2R))) / (cosDec * Math.cos(latitude * D2R));
        double H;
        if (sunrise) {
            H = 360 - R2D * Math.acos(cosH);
        } else {
            H = R2D * Math.acos(cosH);
        }
        H = H / 15;

        //calculate local mean time of rising/setting
        double T = H + RA - (0.06571 * approxTime) - 6.622;

        //adjust back to UTC
        double UT = T - lnHour;
        if (UT > 24) {
            UT = UT - 24;
        } else if (UT < 0) {
            UT = UT + 24;
        }

        //convert UT value to local time zone of latitude/longitude
        double localT = UT + 1;

        //convert to Milliseconds
        return (long) localT * 3600 * 1000;
    }
}
