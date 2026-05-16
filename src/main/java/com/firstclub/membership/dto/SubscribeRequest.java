package com.firstclub.membership.dto;

import jakarta.validation.constraints.NotBlank;

public record SubscribeRequest(
        @NotBlank(message = "Plan name is required") String planName
) {}
