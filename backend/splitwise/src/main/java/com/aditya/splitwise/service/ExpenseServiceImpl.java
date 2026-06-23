package com.aditya.splitwise.service;

import com.aditya.splitwise.dto.BalanceResponse;
import com.aditya.splitwise.dto.CreateExpenseRequest;
import com.aditya.splitwise.dto.ExpenseResponse;
import com.aditya.splitwise.dto.SettlementResponse;
import com.aditya.splitwise.entity.*;
import com.aditya.splitwise.exception.GroupNotFoundException;
import com.aditya.splitwise.exception.InvalidExpenseException;
import com.aditya.splitwise.exception.UserNotFoundException;
import com.aditya.splitwise.repository.ExpenseRepository;
import com.aditya.splitwise.repository.GroupRepository;
import com.aditya.splitwise.repository.UserRepository;
import com.aditya.splitwise.dto.UserBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl
        implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest request) {

        Group group = groupRepository.findById(
                        request.getGroupId())
                .orElseThrow(() ->
                        new GroupNotFoundException(
                                "Group not found"));

        User paidBy = userRepository.findById(
                        request.getPaidByUserId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"));
        

        if (!group.getMembers().contains(paidBy)) {
                throw new InvalidExpenseException(
                        "PaidBy user is not a member of the group");
                }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidExpenseException(
                        "Amount must be greater than zero");
                }

        int participantCount =
                request.getParticipantIds().size();

        if (participantCount == 0) {
            throw new InvalidExpenseException(
                    "No participants provided");
        }

        BigDecimal share =
                request.getAmount()
                        .divide(BigDecimal.valueOf(participantCount),
                                2,
                                RoundingMode.HALF_UP);

        Expense expense = Expense.builder()
                .description(request.getDescription())
                .amount(request.getAmount())
                .group(group)
                .paidBy(paidBy)
                .build();

        // Map to hold userId and their corresponding share
        Map<Long, BigDecimal> shareMap = new HashMap<>();

        // Check for duplicate participants
        Set<Long> uniqueParticipants =
                new HashSet<>(request.getParticipantIds());

        if (uniqueParticipants.size()!= request.getParticipantIds().size()) {
                throw new InvalidExpenseException(
                        "Duplicate participants are not allowed");
        }

        // Validate that all participants are members of the group 
        // and create ExpenseParticipant entities with the calculated share
        for (Long userId : request.getParticipantIds()) {

            User user = userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new UserNotFoundException(
                                            "User not found"));

                if (!group.getMembers().contains(user)) {
                        throw new InvalidExpenseException(
                                "Participant is not a member of the group");
                        }
                        
            ExpenseParticipant participant =
                    ExpenseParticipant.builder()
                            .expense(expense)
                            .user(user)
                            .share(share)
                            .build();

            expense.getParticipants()
                    .add(participant);

            shareMap.put(userId, share);
        }

        Expense savedExpense =
                expenseRepository.save(expense);

        return ExpenseResponse.builder()
                .expenseId(savedExpense.getId())
                .description(savedExpense.getDescription())
                .amount(savedExpense.getAmount())
                .paidByUserId(paidBy.getId())
                .shares(shareMap)
                .build();
    }

        @Override
        @Transactional(readOnly = true)
        public BalanceResponse getGroupBalances(Long groupId) {
                groupRepository.findById(groupId)
                        .orElseThrow(() ->
                                new GroupNotFoundException(
                                        "Group not found"));

                List<Expense> expenses =
                        expenseRepository.findByGroupId(
                                groupId);

                Map<Long, BigDecimal> balances = new HashMap<>();

                for (Expense expense : expenses) {
                        Long payerId = expense.getPaidBy().getId();
                        BigDecimal amount = expense.getAmount();

                        balances.put(payerId,
                                balances.getOrDefault(payerId, BigDecimal.ZERO).add(amount));
                }

                for (Expense expense : expenses) {
                        for (ExpenseParticipant participant : expense.getParticipants()) {
                        Long userId = participant.getUser().getId();

                        balances.put(userId,
                                balances.getOrDefault(userId, BigDecimal.ZERO).subtract(
                                        participant.getShare()
                                        )
                                );
                        }
                }

                return BalanceResponse.builder()
                        .balances(balances)
                        .build();
        }

        @Override
        @Transactional(readOnly = true)
        public List<SettlementResponse> simplifyDebts(
                Long groupId) {
                        BalanceResponse balanceResponse =
                                getGroupBalances(groupId);

                        Map<Long, BigDecimal> balances =
                                balanceResponse.getBalances();

                        List<UserBalance> creditors =
                                new ArrayList<>();

                        List<UserBalance> debtors =
                                new ArrayList<>();

                        for (Map.Entry<Long, BigDecimal> entry
                                        : balances.entrySet()) {

                                BigDecimal amount = entry.getValue();

                                if (amount.compareTo(BigDecimal.ZERO) > 0) {

                                        creditors.add(
                                                new UserBalance(
                                                        entry.getKey(),
                                                        amount));

                                } else if (
                                        amount.compareTo(BigDecimal.ZERO) < 0) {

                                        debtors.add(
                                                new UserBalance(
                                                        entry.getKey(),
                                                        amount.abs()));
                                }
                        }
                        int i=0,j=0;
                        List<SettlementResponse> result =
                                new ArrayList<>();

                        while (i < debtors.size()
                                && j < creditors.size()) {

                        UserBalance debtor = debtors.get(i);

                        UserBalance creditor = creditors.get(j);

                        BigDecimal settlementAmount =
                                debtor.getAmount()
                                        .min(creditor.getAmount());

                        result.add(SettlementResponse.builder()
                                        .fromUserId(
                                                debtor.getUserId())
                                        .toUserId(
                                                creditor.getUserId())
                                        .amount(
                                                settlementAmount)
                                        .build());
                        
                        debtor.setAmount( debtor.getAmount()
                                        .subtract(
                                                settlementAmount));

                        creditor.setAmount(creditor.getAmount()
                                        .subtract(
                                                settlementAmount));
                        
                        if (debtor.getAmount()
                                .compareTo(BigDecimal.ZERO)
                                == 0) {

                        i++;
                        }

                        if (creditor.getAmount()
                                .compareTo(BigDecimal.ZERO)
                                == 0) {

                        j++;
                        }
                        }
                        return result;
                }
}