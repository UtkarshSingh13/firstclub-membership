package com.firstclub.membership.dto;

import com.firstclub.membership.entity.TierBenefit;

public record TierBenefitResponse(Long id, String benefitType, String benefitValue, String description) {
    public static TierBenefitResponse from(TierBenefit benefit) {
        return new TierBenefitResponse(benefit.getId(), benefit.getBenefitType().name(),
                benefit.getBenefitValue(), benefit.getDescription());
    }
}
