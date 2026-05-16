package com.firstclub.membership.dto;

import jakarta.validation.constraints.NotBlank;

public record TierChangeRequest(
        @NotBlank(message = "Tier name is required") String tierName
) {}
