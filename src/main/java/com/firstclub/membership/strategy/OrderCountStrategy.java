package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderCountStrategy implements TierEvaluationStrategy {

    private final OrderRepository orderRepository;

    @Override
    public boolean evaluate(Long userId, TierCriteria criteria) {
        LocalDateTime since = LocalDateTime.now().minusDays(criteria.getEvaluationPeriodDays());
        long count = orderRepository.countByUserIdAndOrderDateAfter(userId, since);
        return count >= Long.parseLong(criteria.getThresholdValue());
    }

    @Override
    public CriteriaType getSupportedCriteriaType() {
        return CriteriaType.MIN_ORDER_COUNT;
    }
}
