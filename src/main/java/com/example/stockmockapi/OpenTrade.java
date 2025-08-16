package com.example.stockmockapi;

import lombok.Data;

@Data
public class OpenTrade {
    Trades.Type type;
    double amount;
    String yyyyMMdd;
    String hhmm;
    double startPrice;
    double bollingerLower;
    double bollingerUpper;

    public OpenTrade(Trades.Type type, double amount, String yyyyMMdd, String hhmm,
                     double starPrice, double bollingerLower, double bollingerUpper) {
        this.type = type;
        this.amount = amount;
        this.yyyyMMdd = yyyyMMdd;
        this.hhmm = hhmm;
        this.startPrice = starPrice;
        this.bollingerLower = bollingerLower;
        this.bollingerUpper = bollingerUpper;
    }
}
