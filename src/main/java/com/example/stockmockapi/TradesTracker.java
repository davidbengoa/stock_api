package com.example.stockmockapi;

import java.util.*;

import static com.example.stockmockapi.Utils.TimeFrames.*;

public class TradesTracker {
    String symbol;
    NavigableMap<String, TradesByMonthOrDay> tradesByDay;
    NavigableMap<String, TradesByMonthOrDay> tradesByMonth;
    List<CompleteTrade> allTrades;
    Map<String, OpenTrade> openTrades;


    public TradesTracker() {
        tradesByDay = new TreeMap<>();
        tradesByMonth = new TreeMap<>();
        openTrades = new HashMap<>();
        allTrades = new ArrayList<>();
    }

    public void trackOrder(String symbol, Trades.Type type, double amount, String yyyyMMdd, String hhmm, double iniBalance,
                           double price, double bollingerLower, double bollingerUpper) {
        if (!tradesByDay.containsKey(yyyyMMdd)) {
            Map.Entry<String, TradesByMonthOrDay> previous = tradesByDay.lowerEntry(yyyyMMdd);
            iniBalance = previous != null ? previous.getValue().endBalance : iniBalance;
            tradesByDay.put(yyyyMMdd, new TradesByMonthOrDay(yyyyMMdd, iniBalance));
        }
        String yyyyMM = yyyyMMdd.substring(0, 6);
        if (!tradesByMonth.containsKey(yyyyMM)) {
            Map.Entry<String, TradesByMonthOrDay> previous = tradesByMonth.lowerEntry(yyyyMM);
            iniBalance = previous != null ? previous.getValue().endBalance : iniBalance;
            tradesByMonth.put(yyyyMM, new TradesByMonthOrDay(yyyyMM, iniBalance));
        }

        if (!openTrades.containsKey(symbol)) {
            // OPEN TRADE
            openTrades.put(symbol, new OpenTrade(type, amount, yyyyMMdd, hhmm, price, bollingerLower, bollingerUpper));
        } else {
            // CLOSE TRADE
            OpenTrade openTrade = openTrades.get(symbol);
            double profitLoss = type.equals(Trades.Type.BUY) ? (openTrade.amount - amount) : (amount - openTrade.amount);
            tradesByDay.get(yyyyMMdd).updateBalance(symbol, profitLoss, openTrade.getHhmm(), hhmm);

            Utils.printLine(tradesByDay.get(yyyyMMdd).toString());
            Trades.Type tradeType = type.equals(Trades.Type.BUY) ? Trades.Type.SELL : Trades.Type.BUY;
            allTrades.add(new CompleteTrade(tradesByDay.get(yyyyMMdd), symbol, tradeType.toString(), openTrade.getStartPrice(),
                    openTrade.getBollingerLower(), openTrade.getBollingerUpper(), price));
            tradesByDay.get(yyyyMMdd).setInitBalancePerTrade(tradesByDay.get(yyyyMMdd).getEndBalance());

            tradesByDay.get(yyyyMMdd).setType(type.equals(Trades.Type.BUY));
            tradesByMonth.get(yyyyMM).updateBalance(symbol, profitLoss, openTrade.getHhmm(), hhmm);
            openTrades.remove(symbol);
        }
    }

    public void displayStats(double initialBalance) {
        Utils.printLine("===== STATS BY DAY =====");
        SortedSet<String> dailyKeys = new TreeSet<>(tradesByDay.keySet());
        for (String yyyyMMdd : dailyKeys) {
            Utils.printLine(tradesByDay.get(yyyyMMdd).toString());
        }
        Utils.printLine("===== STATS BY MONTH =====");
        double endBalance = initialBalance;
        int tradeNumber = 0;
        SortedSet<String> monthlyKeys = new TreeSet<>(tradesByMonth.keySet());
        for (String yyyyMM : monthlyKeys) {
            Utils.printLine(tradesByMonth.get(yyyyMM).toString());
            endBalance = tradesByMonth.get(yyyyMM).getEndBalance();
            tradeNumber += tradesByMonth.get(yyyyMM).getNumberOfTrades();
        }

        double totalProfit = Utils.roundTo2Decimal(endBalance - initialBalance);
        double totalProfitPerc = totalProfit / initialBalance;
        totalProfitPerc = Utils.roundTo2Decimal(totalProfitPerc * 100);
        Utils.printLine("===== ALL STATS =====");
        Utils.printLine("InitialBalance=" + initialBalance +
                ", endBalance=" + Utils.roundTo2Decimal(endBalance) +
                ", totalProfit=" + totalProfit +
                ", totalProfitPerc=" + totalProfitPerc +
                "%, numberOfTrades=" + tradeNumber);
    }

    public void generateCsvTradesByDay(String input) {
        SortedSet<String> dailyKeys = new TreeSet<>(tradesByDay.keySet());
        StringBuilder content = new StringBuilder();
        content.append("Date").append(",");
        content.append("#Trades").append(",");
        content.append("Profit").append(",");
        content.append("Profit %").append(",");
        content.append("Initial Balance").append(",");
        content.append("End Balance").append(",");
        content.append("04:00-09:30").append(",");
        content.append("09:30-10:00").append(",");
        content.append("10:00-11:00").append(",");
        content.append("11:00-12:00").append(",");
        content.append("12:00-13:00").append(",");
        content.append("13:00-14:00").append(",");
        content.append("14:00-15:00").append(",");
        content.append("15:00-16:00").append(",");
        content.append("16:00-20:00").append(",");
        content.append("Total").append(",");
//        content.append("TradeIniBalance").append(",");
//        content.append("TradeEndBalance").append(",");
//        content.append("TradeProfitLoss").append(",");
//        content.append("TradeDiffTime").append(",");
        content.append("\n");
        double totalTrades = 0;
        double totalProfit = 0;
        double totalProfitPerc = 0;
        double total0409 = 0;
        double total0910 = 0;
        double total1011 = 0;
        double total1112 = 0;
        double total1213 = 0;
        double total1314 = 0;
        double total1415 = 0;
        double total1516 = 0;
        double total1620 = 0;
        double totalTotal = 0;
        for (String yyyyMMdd : dailyKeys) {
            TradesByMonthOrDay trade = tradesByDay.get(yyyyMMdd);
            StringBuilder line = new StringBuilder();
            line.append(yyyyMMdd).append(",");
            line.append(trade.getNumberOfTrades()).append(",");
            line.append(trade.getTotalProfit()).append(",");
            line.append(trade.getTotalProfitPerc()).append(",");
            line.append(trade.getInitialBalance()).append(",");
            line.append(trade.getEndBalance()).append(",");

            totalTrades += trade.getNumberOfTrades();
            totalProfit += trade.getTotalProfit();
            totalProfitPerc += trade.getTotalProfitPerc();

            Map<String, Double> timeframe = Utils.getTimeFrames();
            for (String symbolElem: trade.getSymbols().keySet()) {
                Map<String, Double> tfSymbol = trade.getSymbols().get(symbolElem);
                timeframe.put(TF_04_AND_09.name(), Utils.roundTo2Decimal(
                    timeframe.get(TF_04_AND_09.name()) + tfSymbol.get(TF_04_AND_09.name())));
                timeframe.put(TF_09_AND_10.name(), Utils.roundTo2Decimal(
                    timeframe.get(TF_09_AND_10.name()) + tfSymbol.get(TF_09_AND_10.name())));
                timeframe.put(TF_10_AND_11.name(), Utils.roundTo2Decimal(
                        timeframe.get(TF_10_AND_11.name()) + tfSymbol.get(TF_10_AND_11.name())));
                timeframe.put(TF_11_AND_12.name(), Utils.roundTo2Decimal(
                        timeframe.get(TF_11_AND_12.name()) + tfSymbol.get(TF_11_AND_12.name())));
                timeframe.put(TF_12_AND_13.name(), Utils.roundTo2Decimal(
                        timeframe.get(TF_12_AND_13.name()) + tfSymbol.get(TF_12_AND_13.name())));
                timeframe.put(TF_13_AND_14.name(), Utils.roundTo2Decimal(
                        timeframe.get(TF_13_AND_14.name()) + tfSymbol.get(TF_13_AND_14.name())));
                timeframe.put(TF_14_AND_15.name(), Utils.roundTo2Decimal(
                        timeframe.get(TF_14_AND_15.name()) + tfSymbol.get(TF_14_AND_15.name())));
                timeframe.put(TF_15_AND_16.name(), Utils.roundTo2Decimal(
                        timeframe.get(TF_15_AND_16.name()) + tfSymbol.get(TF_15_AND_16.name())));
                timeframe.put(TF_16_AND_20.name(), Utils.roundTo2Decimal(
                        timeframe.get(TF_16_AND_20.name()) + tfSymbol.get(TF_16_AND_20.name())));
                timeframe.put(TF_TOTAL.name(), Utils.roundTo2Decimal(
                        timeframe.get(TF_TOTAL.name()) + tfSymbol.get(TF_TOTAL.name())));
                total0409 += tfSymbol.get(TF_04_AND_09.name());
                total0910 += tfSymbol.get(TF_09_AND_10.name());
                total1011 += tfSymbol.get(TF_10_AND_11.name());
                total1112 += tfSymbol.get(TF_11_AND_12.name());
                total1213 += tfSymbol.get(TF_12_AND_13.name());
                total1314 += tfSymbol.get(TF_13_AND_14.name());
                total1415 += tfSymbol.get(TF_14_AND_15.name());
                total1516 += tfSymbol.get(TF_15_AND_16.name());
                total1620 += tfSymbol.get(TF_16_AND_20.name());
                totalTotal += tfSymbol.get(TF_TOTAL.name());
            }
            line.append(timeframe.get(TF_04_AND_09.name())).append(",");
            line.append(timeframe.get(TF_09_AND_10.name())).append(",");
            line.append(timeframe.get(TF_10_AND_11.name())).append(",");
            line.append(timeframe.get(TF_11_AND_12.name())).append(",");
            line.append(timeframe.get(TF_12_AND_13.name())).append(",");
            line.append(timeframe.get(TF_13_AND_14.name())).append(",");
            line.append(timeframe.get(TF_14_AND_15.name())).append(",");
            line.append(timeframe.get(TF_15_AND_16.name())).append(",");
            line.append(timeframe.get(TF_16_AND_20.name())).append(",");
            line.append(Utils.roundTo2Decimal(timeframe.get(TF_TOTAL.name()))).append(",");

//            line.append(trade.getInitBalancePerTrade()).append(",");
//            line.append(trade.getEndBalance()).append(",");
//            line.append(trade.getEndBalance() - trade.getInitBalancePerTrade()).append(",");
//            line.append(Utils.getDiffTime(trade.getStartTime(), trade.getEndTime())).append(",");

            content.append(line).append("\n");
        }

        content.append("TOTAL:").append(",");
        content.append(Utils.roundTo2Decimal(totalTrades)).append(",");
        content.append(Utils.roundTo2Decimal(totalProfit)).append(",");
        content.append(Utils.roundTo2Decimal(totalProfitPerc)).append(",");
        content.append("").append(",");
        content.append("").append(",");
        content.append(Utils.roundTo2Decimal(total0409)).append(",");
        content.append(Utils.roundTo2Decimal(total0910)).append(",");
        content.append(Utils.roundTo2Decimal(total1011)).append(",");
        content.append(Utils.roundTo2Decimal(total1112)).append(",");
        content.append(Utils.roundTo2Decimal(total1213)).append(",");
        content.append(Utils.roundTo2Decimal(total1314)).append(",");
        content.append(Utils.roundTo2Decimal(total1415)).append(",");
        content.append(Utils.roundTo2Decimal(total1516)).append(",");
        content.append(Utils.roundTo2Decimal(total1620)).append(",");
        content.append(Utils.roundTo2Decimal(totalTotal));

        String now = Utils.customDateTime("yyyyMMdd-HHmmss");
        Utils.createNewFile("stocksByDay_" + input + "_" + now + ".csv", content.toString());
    }

    public void generateCsvAllTrades() {
        StringBuilder content = new StringBuilder();
        content.append("Symbol").append(",");
        content.append("Date").append(",");
        content.append("Initial Balance").append(",");
        content.append("End Balance").append(",");
        content.append("StartTime").append(",");
        content.append("EndTime").append(",");
        content.append("Full Date Time").append(",");
        content.append("Profit/Loss").append(",");
        content.append("DiffTime").append(",");
        content.append("Type").append(",");
        content.append("BollingerLower").append(",");
        content.append("StartPrice").append(",");
        content.append("BollingerUpper").append(",");
        content.append("EndPrice").append(",");
        content.append("\n");
        for (CompleteTrade trade : allTrades) {
            content.append(trade.getSymbol()).append(",");
            content.append(trade.getDateTime()).append(",");
            content.append(trade.getIniBalance()).append(",");
            content.append(trade.getEndBalance()).append(",");
            content.append(trade.getStartTime()).append(",");
            content.append(trade.getEndTime()).append(",");
            content.append("'" + trade.getDateTime() +
                    (trade.getStartTime().length() == 3 ? "0" + trade.getStartTime()
                            : trade.getStartTime())).append(",");
            content.append(trade.getEndBalance() - trade.getIniBalance()).append(",");
            content.append(Utils.getDiffTime(trade.getStartTime(), trade.getEndTime())).append(",");
            content.append(trade.getType()).append(",");
            content.append(Utils.roundTo2Decimal(trade.getBollingerLower())).append(",");
            content.append(Utils.roundTo2Decimal(trade.getStartPrice())).append(",");
            content.append(Utils.roundTo2Decimal(trade.getBollingerUpper())).append(",");
            content.append(Utils.roundTo2Decimal(trade.getEndPrice())).append(",");
            content.append("\n");
        }
        String now = Utils.customDateTime("yyyyMMdd-HHmmss");
        Utils.createNewFile("allStocksByDay_" + now + ".csv", content.toString());
    }

    public void runStats() {
        int minLowerPricePerc = 1;
        int minUpperPricePerc = 1;
        Map<Double,String> allPL = new HashMap<>();
        for (int i=minLowerPricePerc; i<=199; i++) {
            for (int j=minUpperPricePerc; j<=199; j++) {
                double profitLoss = Utils.roundTo4Decimal(runStats(i, j));

                allPL.put(profitLoss, "MinLowerPricePerc: " + Utils.roundTo2Decimal((double) i/100.0)
                        + "%  MinUpperPricePerc: " + Utils.roundTo2Decimal((double) j/100.0)
                        + "%  Profit/Loss: " + profitLoss);
            }
        }
        List<Double> sortedPL = new ArrayList<>(allPL.keySet());
        Collections.sort(sortedPL, Collections.reverseOrder());
        for (int i=0; i<10; i++) {
            Utils.printLine(allPL.get(sortedPL.get(i)));
        }
//        for (double key: allPL.keySet()) {
//            Utils.printLine(allPL.get(key));
//        }
    }

    private double runStats(double minLowerPricePerc, double minUpperPricePerc) {
        minLowerPricePerc = minLowerPricePerc / 100.0;
        minUpperPricePerc = minUpperPricePerc / 100.0;
        double accruedProfit = 0;
        for (CompleteTrade trade : allTrades) {
            double profit = trade.getEndBalance() - trade.getIniBalance();
            String type = trade.getType();
            double lowerPricePerc = ((trade.getStartPrice() / trade.getBollingerLower()) - 1) * 100.0;
            double upperPricePerc = ((trade.getBollingerUpper() / trade.getStartPrice()) - 1) * 100.0;
            boolean execute = false;
            if ((type.equals("SELL") && lowerPricePerc >= minLowerPricePerc) ||
                    (type.equals("BUY") && upperPricePerc >= minUpperPricePerc)) {
                accruedProfit += profit;
                execute = true;
            }

//            if (minLowerPricePerc == 0.01 && minUpperPricePerc == 0.46) {
//                String buyOrSell = type.equals("SELL") && lowerPricePerc >= minLowerPricePerc ? "SELL" : (
//                        type.equals("BUY") && upperPricePerc >= minUpperPricePerc ? "BUY" : "IDLE");
//                Utils.printLine("IniBalance: " + trade.getIniBalance()
//                        + "  EndBalance: " + trade.getEndBalance()
//                        + "  Profit/Loss: " + Utils.roundTo2Decimal(profit)
//                        + "  BollingerLower: " + trade.getBollingerLower()
//                        + "  StartPrice: " + trade.getStartPrice()
//                        + "  BollingerUpper: " + trade.getBollingerUpper()
//                        + "  LowerPricePerc: " + minLowerPricePerc
//                        + "%  UpperPricePerc: " + minUpperPricePerc
//                        + "%  BuyOrSell: " + buyOrSell
//                        + "  Profit/Loss: " + (execute ? profit : 0));
//            }
        }
//        Utils.printLine("MinLowerPricePerc: " + minLowerPricePerc + "%  MinUpperPricePerc: " + minUpperPricePerc
//                + "%  Profit/Loss: " + accruedProfit);
        return accruedProfit;
    }
}
