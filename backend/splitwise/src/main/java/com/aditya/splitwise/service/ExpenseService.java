package com.aditya.splitwise.service;

import com.aditya.splitwise.dto.CreateExpenseRequest;
import com.aditya.splitwise.dto.ExpenseResponse;
import com.aditya.splitwise.dto.BalanceResponse;

public interface ExpenseService {

    ExpenseResponse createExpense(
            CreateExpenseRequest request);

    BalanceResponse getGroupBalances(Long groupId);
}