package com.aditya.splitwise.service;

import com.aditya.splitwise.dto.CreateUserRequest;
import com.aditya.splitwise.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUser(Long id);

    List<UserResponse> getAllUsers();
}