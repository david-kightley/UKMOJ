package com.hmpps.oyster;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class JourneyPlanner {

    private OysterCard oysterCard = null;

    private Map<Integer,Station> stations = new HashMap<>();

    void init(String stationFile) throws Exception {

        try (InputStream is = FareFinder.class.getResourceAsStream(stationFile)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            Station station;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("ID")) {
                    continue;
                }
                String[] sa = line.split(",");
                int id = Integer.parseInt(sa[0]);
                if (sa[1].equals("BUS")) {
                    station = new BusStop(id);
                } else {
                    int zone = Integer.parseInt(sa[2]);
                    station = new Station(id, sa[1], zone);
                }
                this.stations.put(id, station);
            }
        }
    }

    public String takeJourney(int startStation, int finishStation) {
        if (oysterCard == null) {
            return "You do not have a Card registered";
        }
        Station entryStation = stations.get(startStation);
        Station exitStation = stations.get(finishStation);
        if (entryStation == null || exitStation == null) {
            return "Invalid journey: " + (entryStation == null ? "null" : entryStation.getName()) +
                    " - " + (exitStation == null ? "null" : exitStation.getName());
        }
        oysterCard.touchIn(entryStation);
        String fare = oysterCard.touchOut(exitStation);
        return entryStation.getName() + " -> " + exitStation.getName() + " : Fare = " + fare;
    }

    public void registerOysterCard(OysterCard card) {
        this.oysterCard = card;
    }

    public String getCardBalance() {
        if (oysterCard == null) {
            return "No card";
        }
        return oysterCard.toString();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Please specify the Station list file and fares file");
            System.exit(0);
        }
        JourneyPlanner planner = new JourneyPlanner();
        planner.init(args[0]);

        FareFinder.init(args[1]);

        OysterCard card = new OysterCard(123456);
        card.load(30);
        planner.registerOysterCard(card);

        System.out.println(planner.takeJourney(1,2));
        System.out.println(planner.takeJourney(328,5));
        System.out.println(planner.takeJourney(2,4));

        System.out.println(planner.getCardBalance());
    }
}
