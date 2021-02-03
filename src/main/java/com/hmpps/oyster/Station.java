package com.hmpps.oyster;

public class Station {

    private final int id;
    private final String name;
    private final int zone;

    public Station(int id, String name, int zone) {
        this.id = id;
        this.name = name;
        this.zone = zone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return name;
    }
}
