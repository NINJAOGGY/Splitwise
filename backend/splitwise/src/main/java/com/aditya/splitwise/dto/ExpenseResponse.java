package com.aditya.splitwise.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Builder
public class ExpenseResponse {

    private Long expenseId;

    private String description;

    private BigDecimal amount;

    private Long paidByUserId;

    private Map<Long, BigDecimal> shares;
}