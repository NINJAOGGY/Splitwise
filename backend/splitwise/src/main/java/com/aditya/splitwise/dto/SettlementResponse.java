package com.aditya.splitwise.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SettlementResponse {

    private Long fromUserId;

    private Long toUserId;

    private BigDecimal amount;
}