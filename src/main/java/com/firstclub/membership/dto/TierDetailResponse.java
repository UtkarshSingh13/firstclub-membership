package com.firstclub.membership.dto;

import com.firstclub.membership.entity.MembershipTier;
import java.util.List;

public record TierDetailResponse(Long id, String name, Integer rank, String description,
                                  List<TierBenefitResponse> benefits) {
    public static TierDetailResponse from(MembershipTier tier, List<TierBenefitResponse> benefits) {
        return new TierDetailResponse(tier.getId(), tier.getName(), tier.getRank(),
                tier.getDescription(), benefits);
    }
}
