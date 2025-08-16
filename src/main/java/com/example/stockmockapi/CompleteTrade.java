package com.example.stockmockapi;

import lombok.Data;

@Data
public class CompleteTrade {
    String dateTime;
    String symbol;
    double startPrice;
    double endPrice;
    double iniBalance;
    double endBalance;
    double profit;
    String startTime;
    String endTime;
    String diffTime;
    String type;
    double bollingerLower;
    double bollingerUpper;

    public CompleteTrade(TradesByMonthOrDay trade, String symbol, String type, double startPrice, double bollingerLower,
                         double bollingerUpper, double endPrice) {
        this.dateTime = trade.getDateTime();
        this.symbol = symbol;
        this.iniBalance = trade.getInitBalancePerTrade();
        this.endBalance = trade.getEndBalance();
        this.profit = Utils.roundTo2Decimal(endBalance - iniBalance);
        this.startTime = trade.getStartTime();
        this.endTime = trade.getEndTime();
        this.diffTime = Utils.getDiffTime(startTime, endTime);
        this.bollingerLower = bollingerLower;
        this.bollingerUpper = bollingerUpper;
        this.startPrice = startPrice;
        this.endPrice = endPrice;
        this.type = type;
    }
}
