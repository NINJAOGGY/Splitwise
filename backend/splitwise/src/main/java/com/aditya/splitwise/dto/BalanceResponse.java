package com.aditya.splitwise.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Builder
public class BalanceResponse {

    private Map<Long, BigDecimal> balances;
}