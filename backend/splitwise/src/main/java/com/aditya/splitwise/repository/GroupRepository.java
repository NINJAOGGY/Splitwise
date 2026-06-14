package com.aditya.splitwise.repository;

import com.aditya.splitwise.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository
        extends JpaRepository<Group, Long> {
}