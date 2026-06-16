package com.aditya.splitwise.service;

import com.aditya.splitwise.dto.CreateExpenseRequest;
import com.aditya.splitwise.dto.ExpenseResponse;
import com.aditya.splitwise.entity.*;
import com.aditya.splitwise.exception.GroupNotFoundException;
import com.aditya.splitwise.exception.InvalidExpenseException;
import com.aditya.splitwise.exception.UserNotFoundException;
import com.aditya.splitwise.repository.ExpenseRepository;
import com.aditya.splitwise.repository.GroupRepository;
import com.aditya.splitwise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl
        implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public ExpenseResponse createExpense(
            CreateExpenseRequest request) {

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
                        .divide(
                                BigDecimal.valueOf(
                                        participantCount),
                                2,
                                RoundingMode.HALF_UP);

        Expense expense = Expense.builder()
                .description(
                        request.getDescription())
                .amount(
                        request.getAmount())
                .group(group)
                .paidBy(paidBy)
                .build();

        Map<Long, BigDecimal> shareMap =
                new HashMap<>();

        Set<Long> uniqueParticipants =
                new HashSet<>(
                        request.getParticipantIds());

        if (uniqueParticipants.size()
                != request.getParticipantIds().size()) {

        throw new InvalidExpenseException(
                "Duplicate participants are not allowed");
        }

        for (Long userId :
                request.getParticipantIds()) {

            User user =
                    userRepository.findById(userId)
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
}