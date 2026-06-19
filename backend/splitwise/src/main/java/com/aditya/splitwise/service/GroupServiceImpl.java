package com.aditya.splitwise.service;

import com.aditya.splitwise.dto.CreateGroupRequest;
import com.aditya.splitwise.dto.GroupResponse;
import com.aditya.splitwise.entity.Group;
import com.aditya.splitwise.entity.User;
import com.aditya.splitwise.exception.GroupNotFoundException;
import com.aditya.splitwise.exception.UserNotFoundException;
import com.aditya.splitwise.repository.GroupRepository;
import com.aditya.splitwise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    public GroupResponse createGroup(
            CreateGroupRequest request) {

        Group group = Group.builder()
                .name(request.getName())
                .build();

        return mapToResponse(
                groupRepository.save(group)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResponse getGroup(Long groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new GroupNotFoundException(
                                "Group not found"));

        return mapToResponse(group);
    }

    @Override
    @Transactional
    public GroupResponse addMember(
            Long groupId,
            Long userId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new GroupNotFoundException(
                                "Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"));

        group.getMembers().add(user);

        Group savedGroup =
                groupRepository.save(group);

        return mapToResponse(savedGroup);
    }

    private GroupResponse mapToResponse(
            Group group) {

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .memberIds(
                        group.getMembers()
                                .stream()
                                .map(User::getId)
                                .collect(Collectors.toSet())
                )
                .build();
    }
}