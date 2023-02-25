package com.example.currencyexchangeservice.controller;

import com.example.currencyexchangeservice.model.Currency;
import com.example.currencyexchangeservice.repository.CurrencyRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CurrencyController {
    final CurrencyRepository currencyRepository;

    public CurrencyController(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @GetMapping("/")
    public List<Currency> getAllEmployees() {
        return currencyRepository.findAll();
    }

}
