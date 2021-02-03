package com.hmpps.oyster;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class OysterCardTest {

    private final Station station_1 = new Station(1, "Station 1", 1);
    private final Station station_2 = new Station(2, "Station 2", 2);
    private final Station station_3 = new Station(3, "Station 3", 3);
    private final Station station_1A = new Station(4, "Station 1A", 1);
    private final Station station_2A = new Station(5, "Station 2A", 2);
    private final Station station_3A = new Station(6, "Station 3A", 3);

    private final Station station_12 = new Station(7, "Station 12", 12);
    private final Station station_23 = new Station(8, "Station 23", 23);

    private final Station bus_1 = new BusStop(9);
    private final Station bus_2 = new BusStop(10);

    private final int ZONE_1_FARE = 375;
    private final int ZONE_2_FARE = 250;
    private final int ZONE_3_FARE = 200;


    @BeforeClass
    public static void setupFares() throws Exception {
        FareFinder.init("/test-fares.txt");
    }

    @Test
    public void testInitialization() {
	    OysterCard card = new OysterCard(13579);
	    assertEquals(0, card.getBalanceInPence());
    }

    @Test
    public void testLoadBalance() {
        OysterCard card = new OysterCard(13579);
        card.load(5);
        assertEquals(500, card.getBalanceInPence());
        card.load(5);
        assertEquals(1000, card.getBalanceInPence());
        card.load(25);
        assertEquals(3500, card.getBalanceInPence());
    }


    @Test
    public void testFormattedOutput() {
        OysterCard card = new OysterCard(13579);
        String output = getFormattedOutput(card.toString());
        assertEquals("£0.00", output);

        card.load(15);
        output = getFormattedOutput(card.toString());
        assertEquals("£15.00", output);
    }

    @Test
    public void testTouchInAndOut() {
        OysterCard card = new OysterCard(13579);
        card.load(25);
        assertEquals(2500, card.getBalanceInPence());
        card.touchIn(station_3);
        assertEquals(2500 - FareFinder.getInstance().getMaximumFare(false), card.getBalanceInPence());
        card.touchOut(station_3A);
        assertEquals(2500 - ZONE_3_FARE, card.getBalanceInPence());
    }

    @Test
    public void testTouchInAndOutCircularRoute() {
        OysterCard card = new OysterCard(13579);
        card.load(25);
        assertEquals(2500, card.getBalanceInPence());
        card.touchIn(station_3);
        assertEquals(2500 - FareFinder.getInstance().getMaximumFare(false), card.getBalanceInPence());
        card.touchOut(station_3);
        assertEquals(2500 - ZONE_3_FARE, card.getBalanceInPence());
    }

    @Test
    public void testTouchInAndNotOut() {
        final int maxFare = FareFinder.getInstance().getMaximumFare(false);
        OysterCard card = new OysterCard(13579);
        card.load(15);
        assertEquals(1500, card.getBalanceInPence());
        card.touchIn(station_2);
        assertEquals(1500 - maxFare, card.getBalanceInPence());
        card.touchIn(station_3A);
        assertEquals(1500 - (maxFare * 2), card.getBalanceInPence());
        card.touchOut(station_3);
        assertEquals(1500 - maxFare - ZONE_3_FARE, card.getBalanceInPence());
    }

    @Test
    public void testTouchOutWithoutTouchingIn() {
        final int maxFare = FareFinder.getInstance().getMaximumFare(false);
        OysterCard card = new OysterCard(13579);
        card.load(12);
        assertEquals(1200, card.getBalanceInPence());
        card.touchOut(station_2);
        assertEquals(1200 - maxFare, card.getBalanceInPence());
        card.touchIn(station_2A);
        assertEquals(1200 - (maxFare * 2), card.getBalanceInPence());
        card.touchOut(station_2);
        assertEquals(1200 - maxFare - ZONE_2_FARE, card.getBalanceInPence());
    }


    @Test(expected = RuntimeException.class)
    public void testTouchInWithInsufficientBalance() {
        OysterCard card = new OysterCard(13579);
        card.load(1);
        card.touchIn(station_3);
        card.touchOut(station_3A);
        assertEquals(-100, card.getBalanceInPence());
        card.touchIn(station_1);
        fail();  // Should not reach here
    }

    @Test
    public void testTouchInAndOutBusJourney() {
        OysterCard card = new OysterCard(13579);
        card.load(20);
        assertEquals(2000, card.getBalanceInPence());
        card.touchIn(bus_1);
        final int busFareSubtracted = 2000 - FareFinder.getInstance().getMaximumFare(true);
        assertEquals(busFareSubtracted, card.getBalanceInPence());
        card.touchOut(bus_2);
        assertEquals(busFareSubtracted, card.getBalanceInPence());
    }

    @Test
    public void testTouchInAndOutBusJourneyWithTubeStopAtEnd() {
        OysterCard card = new OysterCard(13579);
        card.load(20);
        assertEquals(2000, card.getBalanceInPence());
        card.touchIn(bus_1);
        final int busFareSubtracted = 2000 - FareFinder.getInstance().getMaximumFare(true);
        assertEquals(busFareSubtracted, card.getBalanceInPence());
        card.touchOut(station_1A);
        assertEquals(busFareSubtracted, card.getBalanceInPence());
    }

    private String getFormattedOutput(String line) {
        int index = line.indexOf('£');
        assertTrue(index > 0);
        return line.substring(index);
    }
}
