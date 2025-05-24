package com.dterz.dtos;

import java.math.BigDecimal;
import java.util.Date;

import com.dterz.model.TransactionType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO {

    private long id;

    private BigDecimal amount;

    private Date date;

    private String description;

    private TransactionType type;

    private String userName;

    private String accountName;

}