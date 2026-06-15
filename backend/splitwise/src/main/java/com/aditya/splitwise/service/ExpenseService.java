package com.aditya.splitwise.service;

import com.aditya.splitwise.dto.CreateExpenseRequest;
import com.aditya.splitwise.dto.ExpenseResponse;

public interface ExpenseService {

    ExpenseResponse createExpense(
            CreateExpenseRequest request);
}