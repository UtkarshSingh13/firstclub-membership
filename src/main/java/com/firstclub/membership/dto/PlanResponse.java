package com.firstclub.membership.dto;

import com.firstclub.membership.entity.MembershipPlan;
import java.math.BigDecimal;

public record PlanResponse(Long id, String name, Integer durationInDays, BigDecimal price) {
    public static PlanResponse from(MembershipPlan plan) {
        return new PlanResponse(plan.getId(), plan.getName().name(),
                plan.getDurationInDays(), plan.getPrice());
    }
}
