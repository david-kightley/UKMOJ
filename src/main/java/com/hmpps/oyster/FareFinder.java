package com.hmpps.oyster;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class FareFinder {

    private static FareFinder INSTANCE = null;

    private Map<Integer, int[]> faresByZone;
    private int busFare;
    private int maxFare;

    private FareFinder() {
        // Private constructor
    }

    public static void init(String faresFile) throws Exception {
        INSTANCE = new FareFinder();

        Map<Integer, int[]> fares = new HashMap<>();

        try (InputStream is = FareFinder.class.getResourceAsStream(faresFile)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            int maxFare = 0;
            while ((line = reader.readLine()) != null) {
                   if (line.isEmpty() || line.startsWith("ZONE")) {
                       continue;
                   }
                   String[] sa = line.split(",");
                   if (sa[0].equals("BUS")) {
                       INSTANCE.busFare = Integer.parseInt(sa[1]);
                   } else {
                        int zone1 = Integer.parseInt(sa[0]);
                        int zone2 = Integer.parseInt(sa[1]);
                        int fare = Integer.parseInt(sa[2]);
                        maxFare = Math.max(maxFare, fare);
                        int[] fareArray = fares.computeIfAbsent(zone1, K -> new int[3]);
                       fareArray[zone2 - 1] = fare;
                       if (zone1 != zone2) {
                           fareArray = fares.computeIfAbsent(zone2, K -> new int[3]);
                           fareArray[zone1 - 1] = fare;
                       }
                   }
            }
            INSTANCE.maxFare = maxFare;
        }
        INSTANCE.faresByZone = fares;
    }

    public int getDifferentialFare(int fare) {
        return maxFare - fare;
    }

    public int calculateFare(Station entryStation, Station exitStation) {
        if (isBusStop(entryStation) || isBusStop(exitStation)) {
            return busFare;
        }
        int entryZone = entryStation.getZone();
        int exitZone = exitStation.getZone();
        return calculateFareForBestZone(entryZone, exitZone);
    }

    private boolean isBusStop(Station station) {
        return station instanceof BusStop;
    }

    private int calculateFareForBestZone(int entryZone, int exitZone) {
        if (entryZone > 9) {
            return Math.min(calculateFareForBestZone(entryZone/10, exitZone), calculateFareForBestZone(entryZone%10, exitZone));
        } else {
            if (exitZone > 9) {
                return Math.min(getFareByZone(entryZone, exitZone/10), getFareByZone(entryZone, exitZone%10));
            }
            return getFareByZone(entryZone, exitZone);
        }
    }

    private int getFareByZone(int z1, int z2) {
        return faresByZone.get(z1)[z2 - 1];
    }

    public int getMaximumFare(boolean bus) {
        return bus ? busFare : maxFare;
    }

    public static FareFinder getInstance() {
        if (INSTANCE == null) {
            throw new NullPointerException("FareFinder not initialised");
        }
        return INSTANCE;
    }

}
