package com.firstclub.membership.service;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.TierCriteriaRepository;
import com.firstclub.membership.strategy.TierEvaluationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TierEvaluator {

    private final Map<CriteriaType, TierEvaluationStrategy> strategies;
    private final MembershipTierRepository tierRepository;
    private final TierCriteriaRepository criteriaRepository;

    public TierEvaluator(List<TierEvaluationStrategy> strategyList,
                         MembershipTierRepository tierRepository,
                         TierCriteriaRepository criteriaRepository) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(TierEvaluationStrategy::getSupportedCriteriaType, Function.identity()));
        this.tierRepository = tierRepository;
        this.criteriaRepository = criteriaRepository;
    }

    public MembershipTier evaluateHighestTier(Long userId) {
        List<MembershipTier> tiers = tierRepository.findAllByOrderByRankDesc();

        for (MembershipTier tier : tiers) {
            List<TierCriteria> criteriaList = criteriaRepository.findByTierId(tier.getId());

            if (criteriaList.isEmpty()) {
                continue;
            }

            for (TierCriteria criteria : criteriaList) {
                TierEvaluationStrategy strategy = strategies.get(criteria.getCriteriaType());
                if (strategy != null && strategy.evaluate(userId, criteria)) {
                    return tier;
                }
            }
        }

        return tiers.get(tiers.size() - 1);
    }
}
