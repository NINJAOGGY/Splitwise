package com.aditya.splitwise.controller;

import com.aditya.splitwise.dto.ApiResponse;
import com.aditya.splitwise.dto.CreateGroupRequest;
import com.aditya.splitwise.dto.GroupResponse;
import com.aditya.splitwise.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ApiResponse<GroupResponse> createGroup(
            @Valid
            @RequestBody
            CreateGroupRequest request) {

        return ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Group created successfully")
                .data(
                        groupService.createGroup(
                                request))
                .build();
    }

    @GetMapping("/{groupId}")
    public ApiResponse<GroupResponse> getGroup(
            @PathVariable Long groupId) {

        return ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Group fetched successfully")
                .data(
                        groupService.getGroup(
                                groupId))
                .build();
    }

    @PostMapping("/{groupId}/members/{userId}")
    public ApiResponse<GroupResponse> addMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {

        return ApiResponse.<GroupResponse>builder()
                .success(true)
                .message("Member added successfully")
                .data(
                        groupService.addMember(
                                groupId,
                                userId))
                .build();
    }
}