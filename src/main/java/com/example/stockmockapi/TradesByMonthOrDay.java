package com.example.stockmockapi;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static com.example.stockmockapi.Utils.TimeFrames.*;

@Data
public class TradesByMonthOrDay {
    String dateTime;
    double numberOfTrades;
    double totalProfit;
    double totalProfitPerc;
    double initialBalance;
    double endBalance;
    double initBalancePerTrade;
    double endBalancePerTrade;
    String startTime;
    String endTime;
    Map<String, Map<String, Double>> symbols;
    Utils.TradeType tradeType;

    public TradesByMonthOrDay(String dateTime, double newBalance) {
        this.dateTime = dateTime;
        numberOfTrades = 0;
        totalProfit = 0;
        totalProfitPerc = 0;
        startTime = "";
        endTime = "";
        initialBalance = Utils.roundTo2Decimal(newBalance);
        initBalancePerTrade = initialBalance;
        endBalance = initialBalance;
        symbols = new HashMap<>();
    }

    /*
    * We are tracking the P/L, P/L% PER DAY, and SYMBOL MOVEMENT
    * */
    public void updateBalance(String symbol, double amount, String startTime, String endTime) {
        numberOfTrades += 1;
        endBalance = Utils.roundTo2Decimal(endBalance + amount);
        totalProfit = Utils.roundTo2Decimal(endBalance - initialBalance);
        totalProfitPerc = totalProfit / initialBalance;
        totalProfitPerc = Utils.roundTo2Decimal(totalProfitPerc * 100);
        this.startTime = startTime;
        this.endTime = endTime;

        Map<String, Double> timeframe = null;
        if (symbols.containsKey(symbol)) {
            timeframe = symbols.get(symbol);
        } else {
            timeframe = Utils.getTimeFrames();
        }

        int currentTime = Integer.parseInt(startTime);
        if (currentTime < 930) {
            timeframe.put(TF_04_AND_09.name(), Utils.roundTo2Decimal(
                    timeframe.getOrDefault(TF_04_AND_09.name(), 0.0) + amount));
        } else if (currentTime < 1000) {
            timeframe.put(TF_09_AND_10.name(), Utils.roundTo2Decimal(
                    timeframe.getOrDefault(TF_09_AND_10.name(), 0.0) + amount));
        } else if (currentTime < 1100) {
            timeframe.put(TF_10_AND_11.name(), Utils.roundTo2Decimal(
                    timeframe.getOrDefault(TF_10_AND_11.name(), 0.0) + amount));
        } else if (currentTime < 1200) {
            timeframe.put(TF_11_AND_12.name(), Utils.roundTo2Decimal(
                    timeframe.getOrDefault(TF_11_AND_12.name(), 0.0) + amount));
        } else if (currentTime < 1300) {
            timeframe.put(TF_12_AND_13.name(), Utils.roundTo2Decimal(
                    timeframe.getOrDefault(TF_12_AND_13.name(), 0.0) + amount));
        } else if (currentTime < 1400) {
            timeframe.put(TF_13_AND_14.name(), Utils.roundTo2Decimal(
                    timeframe.getOrDefault(TF_13_AND_14.name(), 0.0) + amount));
        } else if (currentTime < 1500) {
            timeframe.put(TF_14_AND_15.name(), Utils.roundTo2Decimal(
                    timeframe.getOrDefault(TF_14_AND_15.name(), 0.0) + amount));
        } else if (currentTime <= 1600) {
            timeframe.put(TF_15_AND_16.name(), Utils.roundTo2Decimal(
                    timeframe.getOrDefault(TF_15_AND_16.name(), 0.0) + amount));
        } else {
            timeframe.put(TF_16_AND_20.name(), Utils.roundTo2Decimal(
                    timeframe.getOrDefault(TF_16_AND_20.name(), 0.0) + amount));
        }
        timeframe.put(TF_TOTAL.name(), Utils.roundTo2Decimal(
                timeframe.getOrDefault(TF_TOTAL.name(), 0.0) + amount));
        symbols.put(symbol, timeframe);
    }

    public void setType(boolean isLong) {
        this.tradeType = isLong ? Utils.TradeType.LONG : Utils.TradeType.SHORT;
    }

    @Override
    public String toString() {
        return "TradesByMonthOrDay{" +
                "dateTime= '" + dateTime + '\'' +
                ", numberOfTrades= " + numberOfTrades +
                ", totalProfit= " + totalProfit +
                ", totalProfitPerc= " + totalProfitPerc +
                "%, initialBalance= " + initialBalance +
                ", initBalancePerTrade= " + initBalancePerTrade +
                ", endBalance= " + endBalance +
                ", profitPerTrade= " + Utils.roundTo2Decimal(endBalance - initBalancePerTrade) +
                ", startTime= " + startTime +
                ", endTime= " + endTime +
                ", diffMin= " + Utils.getDiffTime(startTime, endTime) +
                ", symbols= " + symbols +
                '}';
    }
}
