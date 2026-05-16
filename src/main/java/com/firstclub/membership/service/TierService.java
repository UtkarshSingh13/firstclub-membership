package com.firstclub.membership.service;

import com.firstclub.membership.dto.TierBenefitResponse;
import com.firstclub.membership.dto.TierDetailResponse;
import com.firstclub.membership.exception.TierNotFoundException;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.TierBenefitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TierService {

    private final MembershipTierRepository tierRepository;
    private final TierBenefitRepository benefitRepository;

    public List<TierDetailResponse> getAllTiers() {
        return tierRepository.findAllByOrderByRankAsc().stream()
                .map(tier -> {
                    List<TierBenefitResponse> benefits = benefitRepository.findByTierId(tier.getId())
                            .stream().map(TierBenefitResponse::from).toList();
                    return TierDetailResponse.from(tier, benefits);
                })
                .toList();
    }

    public List<TierBenefitResponse> getTierBenefits(Long tierId) {
        if (!tierRepository.existsById(tierId)) {
            throw new TierNotFoundException("Tier with id " + tierId);
        }
        return benefitRepository.findByTierId(tierId).stream()
                .map(TierBenefitResponse::from)
                .toList();
    }
}
