package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CohortStrategy implements TierEvaluationStrategy {

    private final UserRepository userRepository;

    @Override
    public boolean evaluate(Long userId, TierCriteria criteria) {
        return userRepository.findById(userId)
                .map(user -> criteria.getThresholdValue().equals(user.getCohort()))
                .orElse(false);
    }

    @Override
    public CriteriaType getSupportedCriteriaType() {
        return CriteriaType.COHORT;
    }
}
