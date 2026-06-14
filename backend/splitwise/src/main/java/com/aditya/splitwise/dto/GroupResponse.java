package com.aditya.splitwise.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class GroupResponse {

    private Long id;

    private String name;

    private Set<Long> memberIds;
}