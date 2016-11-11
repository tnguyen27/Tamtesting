package com.metlife.poc.util;

/**
 * Jason Winn
 * http://jasonwinn.org
 * Created July 10, 2013
 *
 * Description: Small class that provides approximate distance between
 * two points using the Haversine formula.
 *
 * Call in a static context:
 * Haversine.distance(47.6788206, -122.3271205,
 * 47.6788206, -122.5271205)
 * --> 14.973190481586224 [km]
 */

public final class Haversine {
    /**
     * Harvesine.
     */
    private Haversine() {
    }

    /**
     * Approx Earth radius in KM.
     */
    private static final int EARTH_RADIUS_KM = 6371;
    /**
     * Approx Earth radius in MILES.
     */
    private static final int EARTH_RADIUS_MI = 3959;
    /**
     * String MI = "mi".
     */
    private static final String MI = "mi";

    /**
     * Calculate distance from two lat-long points.
     *
     * @param startLat  Start latitude point
     * @param startLong Start longitude point
     * @param endLat    End latitude point
     * @param endLong   End longitude point
     * @param units     Unit i.e: mi, km
     * @return the distance between the given points
     */
    public static double distance(final double startLat, final double startLong,
                                  final double endLat, final double endLong,
                                  final String units) {

        final double dLat = Math.toRadians((endLat - startLat));
        final double dLong = Math.toRadians((endLong - startLong));
        final double a = haversin(dLat) + Math.cos(Math.toRadians(startLat))
                * Math.cos(Math.toRadians(endLat)) * haversin(dLong);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        final double distanceResult;
        if (MI.equals(units)) {
            distanceResult = EARTH_RADIUS_MI * c;
        } else {
            distanceResult = EARTH_RADIUS_KM * c;
        }
        return distanceResult;
    }

    /**
     * Harvesine.
     *
     * @param val distance
     * @return the harvesine
     */
    public static double haversin(final double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}
