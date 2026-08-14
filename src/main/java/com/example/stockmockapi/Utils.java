package com.example.stockmockapi;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.stockmockapi.Utils.TimeFrames.*;
import static com.example.stockmockapi.Utils.TimeFrames.TF_15_AND_16;

public class Utils {
    public enum TimeFrames {
        TF_04_AND_09,
        TF_09_AND_10,
        TF_10_AND_11,
        TF_11_AND_12,
        TF_12_AND_13,
        TF_13_AND_14,
        TF_14_AND_15,
        TF_15_AND_16,
        TF_16_AND_20,
        TF_TOTAL
    }

    public enum TradeType {
        LONG,
        SHORT
    }

    public static Map<String, Double> getTimeFrames() {
        Map<String, Double> timeframe = new HashMap<>();
        timeframe.put(TF_TOTAL.name(), 0.0);
        timeframe.put(TF_04_AND_09.name(), 0.0);
        timeframe.put(TF_09_AND_10.name(), 0.0);
        timeframe.put(TF_10_AND_11.name(), 0.0);
        timeframe.put(TF_11_AND_12.name(), 0.0);
        timeframe.put(TF_12_AND_13.name(), 0.0);
        timeframe.put(TF_13_AND_14.name(), 0.0);
        timeframe.put(TF_14_AND_15.name(), 0.0);
        timeframe.put(TF_15_AND_16.name(), 0.0);
        timeframe.put(TF_16_AND_20.name(), 0.0);
        return timeframe;
    }

    public static double roundTo2Decimal(double input) {
        return BigDecimal.valueOf(input).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    public static double roundTo4Decimal(double input) {
        return BigDecimal.valueOf(input).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    public static void printLine(String str) {
        System.out.println(currentDateTime(false) + "\t" + str);
    }

    public static String currentDateTime(boolean print) {
        Date date = new Date();
        String curDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(date);
        if (print) {
            System.out.println(curDate);
        }
        return curDate;
    }

    public static String customDateTime(String format) {
        Date date = new Date();
        String curDate = new SimpleDateFormat(format).format(date);
        return curDate;
    }

    public static String getDateTime(long input) {
        Date date = new Date(input);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        return sdf.format(date);
    }

    public static void printLine(Map<String, Object> input) {
        input.forEach((key, value) -> {
            printLine(key + " => " + value);
        });
    }

    public static String getDiffTime(String start, String end) {
        String startHhmm = padHhmm(start);
        String endHhmm = padHhmm(end);
        int startTime = Integer.parseInt(startHhmm.substring(0, 2)) * 60 + Integer.parseInt(startHhmm.substring(2));
        int endTime = Integer.parseInt(endHhmm.substring(0, 2)) * 60 + Integer.parseInt(endHhmm.substring(2));
        return String.valueOf(endTime - startTime);
    }

    public static String padHhmm(String hhmm) {
        if (hhmm == null || hhmm.isEmpty()) {
            return "0000";
        }
        if (hhmm.length() >= 4) {
            return hhmm.substring(0, 4);
        }
        return String.format("%04d", Integer.parseInt(hhmm));
    }

    public static void printError(String str) {
        System.err.println(currentDateTime(false) + "\t" + str);
    }

    public static void createNewFile(String filename, String content) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            Utils.printError("Error creating file: " + filename);
        }
    }
}
