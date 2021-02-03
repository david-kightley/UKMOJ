package com.hmpps.oyster;

import org.apache.log4j.Logger;

import java.math.BigDecimal;

public class OysterCard {
    private static final Logger log = Logger.getLogger(OysterCard.class);

    private final long id;
    private int balanceInPence = 0;
    private Station entryPoint;
    private boolean onBus = false;


    public OysterCard(long id) {
        this.id = id;
    }

    public boolean load(int pounds) {
        if (pounds <= 0) {
            log.warn("Invalid amount to load onto card: £" + pounds);
            return false;
        }
        this.balanceInPence = balanceInPence + (pounds * 100);
        log.info("Loaded " + formatAmount(pounds) + " onto Oyster Card.  New balance: " + formatAmount(this.balanceInPence));
        return true;
    }

    public void touchIn(Station station) {
        if (this.entryPoint != null) {
            log.warn("User did not touch-out - maximum fare charged for last journey");
        }
        if (this.getBalanceInPence() < 0) {
            throw new RuntimeException("Insufficient balance on Card: " + formatAmount(this.balanceInPence));
        }
        setEntryPoint(station);
        this.balanceInPence -= FareFinder.getInstance().getMaximumFare(this.onBus);
        log.info("Touched-In at " + station);
    }

    public String touchOut(Station exitStation) {
        int fare = FareFinder.getInstance().getMaximumFare(onBus);
        if (this.entryPoint == null) {
            log.warn("User did not touch-in - maximum fare charged for this journey");
            this.balanceInPence -= fare;
        } else if (!onBus) {
            fare = FareFinder.getInstance().calculateFare(this.entryPoint, exitStation);
            log.info("Touched-Out at " + exitStation + " Fare for journey: " + formatAmount(fare));
            this.balanceInPence += FareFinder.getInstance().getDifferentialFare(fare);
        }
        reset();
        log.info("Current Card Balance: " + formatAmount(this.balanceInPence));
        return formatAmount(fare);
    }

    public int getBalanceInPence() {
        return balanceInPence;
    }

    private void setEntryPoint(Station station) {
        this.entryPoint = station;
        this.onBus = (station instanceof BusStop);
    }

    private void reset() {
        this.entryPoint = null;
        this.onBus = false;
    }

    private String formatAmount(int amount) {
        StringBuilder sb = new StringBuilder("£");
        sb.append(amount / 100).append(".");
        int pence = amount % 100;
        if (pence < 10) {
            sb.append(0);
        }
        sb.append(pence);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Oyster Card - balance " + formatAmount(this.balanceInPence);
    }
}
