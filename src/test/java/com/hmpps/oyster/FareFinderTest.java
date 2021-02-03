package com.hmpps.oyster;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FareFinderTest {

    private final Station station_1 = new Station(1, "Station 1", 1);
    private final Station station_2 = new Station(2, "Station 2", 2);
    private final Station station_3 = new Station(3, "Station 3", 3);
    private final Station station_1A = new Station(4, "Station 1A", 1);
    private final Station station_2A = new Station(5, "Station 2A", 2);
    private final Station station_3A = new Station(6, "Station 3A", 3);

    private final Station station_12 = new Station(7, "Station 12", 12);
    private final Station station_23 = new Station(8, "Station 23", 23);

    private final Station bus = new BusStop(10);

    private final int MAX_FARE = 475;
    private final int BUS_FARE = 123;

    @BeforeClass
    public static void setupFares() throws Exception {
        FareFinder.init("/test-fares.txt");
    }

    @Test
    public void testInitialization() {
        assertNotNull(FareFinder.getInstance());
    }

    @Test
    public void testFixedFares() {
        assertEquals(MAX_FARE, FareFinder.getInstance().getMaximumFare(false));
        assertEquals(BUS_FARE, FareFinder.getInstance().getMaximumFare(true));
    }

    @Test
    public void testDifferentialFare() {
        assertEquals(MAX_FARE - BUS_FARE, FareFinder.getInstance().getDifferentialFare(BUS_FARE));
    }

    @Test
    public void testFareSingleZone() {
        assertEquals(375, FareFinder.getInstance().calculateFare(station_1, station_1A));
        assertEquals(250, FareFinder.getInstance().calculateFare(station_2, station_2A));
        assertEquals(200, FareFinder.getInstance().calculateFare(station_3, station_3A));
    }

    @Test
    public void testFareCrossZone() {
        assertEquals(425, FareFinder.getInstance().calculateFare(station_1, station_2A));
        assertEquals(325, FareFinder.getInstance().calculateFare(station_2, station_3A));
        assertEquals(475, FareFinder.getInstance().calculateFare(station_1, station_3A));

        assertEquals(425, FareFinder.getInstance().calculateFare(station_2, station_1A));
        assertEquals(325, FareFinder.getInstance().calculateFare(station_3, station_2A));
        assertEquals(475, FareFinder.getInstance().calculateFare(station_3, station_1A));
    }

    @Test
    public void testFareOnBus() {
        assertEquals(123, FareFinder.getInstance().calculateFare(station_1, bus));
        assertEquals(123, FareFinder.getInstance().calculateFare(station_2, bus));
        assertEquals(123, FareFinder.getInstance().calculateFare(station_3, bus));

        assertEquals(123, FareFinder.getInstance().calculateFare(bus, station_1));
        assertEquals(123, FareFinder.getInstance().calculateFare(bus, station_2));
        assertEquals(123, FareFinder.getInstance().calculateFare(bus, station_3));

        assertEquals(123, FareFinder.getInstance().calculateFare(bus, bus));
    }

    @Test
    public void testFareWithCrossZoneStations() {
        assertEquals(250, FareFinder.getInstance().calculateFare(station_12, station_2A));
        assertEquals(250, FareFinder.getInstance().calculateFare(station_2, station_12));

        assertEquals(375, FareFinder.getInstance().calculateFare(station_12, station_1A));
        assertEquals(375, FareFinder.getInstance().calculateFare(station_1, station_12));

        assertEquals(250, FareFinder.getInstance().calculateFare(station_23, station_2A));
        assertEquals(250, FareFinder.getInstance().calculateFare(station_2, station_23));

        assertEquals(425, FareFinder.getInstance().calculateFare(station_23, station_1A));
        assertEquals(425, FareFinder.getInstance().calculateFare(station_1, station_23));

        assertEquals(250, FareFinder.getInstance().calculateFare(station_23, station_12));
        assertEquals(250, FareFinder.getInstance().calculateFare(station_12, station_23));
    }
}
