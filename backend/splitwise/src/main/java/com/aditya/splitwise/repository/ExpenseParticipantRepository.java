package com.aditya.splitwise.repository;

import com.aditya.splitwise.entity.ExpenseParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseParticipantRepository
        extends JpaRepository<ExpenseParticipant, Long> {
}