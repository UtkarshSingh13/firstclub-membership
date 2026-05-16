package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.enums.CriteriaType;

public interface TierEvaluationStrategy {
    boolean evaluate(Long userId, TierCriteria criteria);
    CriteriaType getSupportedCriteriaType();
}
