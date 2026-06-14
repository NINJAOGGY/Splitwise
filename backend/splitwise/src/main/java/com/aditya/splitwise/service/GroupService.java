package com.aditya.splitwise.service;

import com.aditya.splitwise.dto.CreateGroupRequest;
import com.aditya.splitwise.dto.GroupResponse;

public interface GroupService {

    GroupResponse createGroup(
            CreateGroupRequest request);

    GroupResponse getGroup(Long groupId);

    GroupResponse addMember(
            Long groupId,
            Long userId);
}