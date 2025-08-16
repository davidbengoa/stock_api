package com.example.stockmockapi;

import lombok.Data;

@Data
public class Trades {
    public enum Type {
        BUY,
        SELL
    }
    public double initialBalance = 100000; // $100k
    private static Trades instance;
    private double balance;
    private TradesTracker tradesTracker;

    private Trades() {
        balance = 0;
        tradesTracker = new TradesTracker();
    }

    public static synchronized Trades getInstance() {
        if (instance == null) {
            instance = new Trades();
        }
        return instance;
    }

    public void init() {
        this.balance = initialBalance;
        tradesTracker = new TradesTracker();
    }

    public void placeTrade(String symbol, Type type, double amount, String dateTime, double price,
                           double bollingerLower, double bollingerUpper) {
        double iniBalance = balance;
        if (type.equals(Type.BUY)) {
            this.balance -= amount;
        } else  {
            this.balance += amount;
        }
        tradesTracker.trackOrder(symbol, type, amount, dateTime.substring(0, 8),
                dateTime.substring(8), iniBalance, price, bollingerLower, bollingerUpper);
    }

    public void showStats() {
        tradesTracker.displayStats(initialBalance);
    }

    public void csvTrades(String timeframe) {
        tradesTracker.generateCsvTradesByDay(timeframe);
    }

    public void csvAllTrades() {
        tradesTracker.generateCsvAllTrades();
    }

    public void runStats() {
        tradesTracker.runStats();
    }
}
