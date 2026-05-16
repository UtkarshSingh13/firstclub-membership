package com.firstclub.membership.dto;

import jakarta.validation.constraints.NotBlank;

public record PlanChangeRequest(
        @NotBlank(message = "Plan name is required") String planName
) {}
