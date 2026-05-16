package com.firstclub.membership.dto;

import com.firstclub.membership.entity.UserSubscription;
import java.time.LocalDate;
import java.util.List;

public record SubscriptionResponse(Long id, Long userId, String planName, String tierName,
                                    LocalDate startDate, LocalDate endDate, String status,
                                    List<TierBenefitResponse> benefits) {
    public static SubscriptionResponse from(UserSubscription sub, List<TierBenefitResponse> benefits) {
        return new SubscriptionResponse(sub.getId(), sub.getUser().getId(),
                sub.getPlan().getName().name(), sub.getTier().getName(),
                sub.getStartDate(), sub.getEndDate(), sub.getStatus().name(), benefits);
    }
}
