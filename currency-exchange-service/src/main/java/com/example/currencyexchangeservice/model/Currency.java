package com.example.currencyexchangeservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="currency")
@Getter
@Setter
public class Currency {
    @Id
    @Column(name="currency_numeric_code")
    private int id;

    @Column(name="currency")
    private String currency;

    @Column(name="currency_alpha_code")
    private String currency_alpha_code;

}
