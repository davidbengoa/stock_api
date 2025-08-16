package com.example.stockmockapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="")
public class TestApi {
    Trades trades;

    @Autowired
    public TestApi() {
        trades = Trades.getInstance();
    }

    @GetMapping(path="/test")
    public ResponseEntity<Double> test() {
        double balance = 0;
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @PostMapping(path="/init")
    public ResponseEntity<String> loadInitialData() {
        trades.init();
        Utils.printLine("============= INITIALIZE BALANCE =============");
        return new ResponseEntity<>("Done!", HttpStatus.OK);
    }

    @GetMapping(path="/balance")
    public ResponseEntity<Double> getBalance() {
        double balance = trades.getBalance();
        balance = Utils.roundTo2Decimal(balance);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @GetMapping(path="/stats")
    public ResponseEntity<String> showStats() {
        trades.showStats();
        return new ResponseEntity<>("Done!", HttpStatus.OK);
    }

    @PostMapping(path="/trade", consumes="application/json", produces="application/json")
    public ResponseEntity<String> placeTrade(@RequestBody Trade body) {
        double cost = body.getShares() * body.getPrice();
        Trades.Type type = Trades.Type.valueOf(body.getAction());
        trades.placeTrade(body.getSymbol(), type, cost, body.getDate(), body.getPrice(),
                body.getBollingerLower(), body.getBollingerUpper());
        //Utils.printLine(body.toString() + " - Balance: " + Utils.roundTo2Decimal(trades.getBalance()));
        return new ResponseEntity<>("Done!", HttpStatus.OK);
    }

    @PostMapping(path="/setInitialBalance", consumes="application/json", produces="application/json")
    public ResponseEntity<String> setInitialBalance(@RequestBody double newBalance) {
        trades.setInitialBalance(newBalance);
        Utils.printLine("Set initial balance: " + newBalance);
        return new ResponseEntity<>("Done!", HttpStatus.OK);
    }

    @GetMapping(path="/getInitialBalance")
    public ResponseEntity<Double> getInitialBalance() {
        double iniBalance = trades.getInitialBalance();
        Utils.printLine("Initial balance: " + iniBalance);
        return new ResponseEntity<>(iniBalance, HttpStatus.OK);
    }

    @PostMapping(path="/csvTrades", consumes="application/json", produces="application/json")
    public ResponseEntity<String> csvTrades(@RequestBody String timeframe) {
        Utils.printLine(timeframe);
        trades.csvTrades(timeframe);
        Utils.printLine("Daily Trades CSV Generated! - " + timeframe);
        return new ResponseEntity<>("Done!", HttpStatus.OK);
    }

    @PostMapping(path="/csvAllTrades", consumes="application/json", produces="application/json")
    public ResponseEntity<String> csvAllTrades() {
        trades.csvAllTrades();
        Utils.printLine("ALL Trades CSV Generated!");
        return new ResponseEntity<>("Done!", HttpStatus.OK);
    }

    @PostMapping(path="/runStats", consumes="application/json", produces="application/json")
    public ResponseEntity<String> runStats() {
        trades.runStats();
        Utils.printLine("Run Stats!");
        return new ResponseEntity<>("Done!", HttpStatus.OK);
    }
}
