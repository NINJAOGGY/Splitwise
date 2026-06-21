package com.aditya.splitwise.service;

import com.aditya.splitwise.dto.CreateExpenseRequest;
import com.aditya.splitwise.dto.ExpenseResponse;
import com.aditya.splitwise.dto.BalanceResponse;
import com.aditya.splitwise.dto.SettlementResponse;
import java.util.List;

public interface ExpenseService {

    ExpenseResponse createExpense(
            CreateExpenseRequest request);

    BalanceResponse getGroupBalances(Long groupId);

    List<SettlementResponse> simplifyDebts(Long groupId);
}