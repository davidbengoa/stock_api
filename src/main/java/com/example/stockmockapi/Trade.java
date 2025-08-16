package com.example.stockmockapi;

import lombok.Data;

@Data
public class Trade {
    String symbol;
    int shares;
    String action;
    double price;
    String date;
    double bollingerLower;
    double bollingerUpper;

    public Trade(String symbol, int shares, String action, double price, String date, double bollingerLower,
                 double bollingerUpper) {
        this.symbol = symbol;
        this.shares = shares;
        this.action = action;
        this.price = price;
        this.date = date;
        this.bollingerLower = bollingerLower;
        this.bollingerUpper = bollingerUpper;

    }

    @Override
    public String toString() {
        return "Trade{" +
                "symbol='" + symbol + '\'' +
                ", shares=" + shares +
                ", action='" + action + '\'' +
                ", price=" + price +
                ", date='" + date + '\'' +
                '}';
    }
}
