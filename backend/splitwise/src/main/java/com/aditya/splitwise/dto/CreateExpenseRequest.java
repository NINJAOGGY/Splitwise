package com.aditya.splitwise.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateExpenseRequest {

    private String description;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Long paidByUserId;

    @NotNull
    private Long groupId;

    @NotEmpty
    private List<Long> participantIds;
}