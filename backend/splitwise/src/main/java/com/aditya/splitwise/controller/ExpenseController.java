package com.aditya.splitwise.controller;

import com.aditya.splitwise.dto.*;
import com.aditya.splitwise.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ApiResponse<ExpenseResponse>
    createExpense(
            @Valid
            @RequestBody
            CreateExpenseRequest request) {

        return ApiResponse.<ExpenseResponse>builder()
                .success(true)
                .message(
                        "Expense created successfully")
                .data(
                        expenseService.createExpense(
                                request))
                .build();
    }

    @GetMapping("/groups/{groupId}/balances")
        public ApiResponse<BalanceResponse>
        getBalances(
                @PathVariable Long groupId) {

        return ApiResponse
                .<BalanceResponse>builder()
                .success(true)
                .message(
                        "Balances fetched successfully")
                .data(
                        expenseService
                                .getGroupBalances(
                                        groupId))
                .build();
        }
}