package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderValueStrategy implements TierEvaluationStrategy {

    private final OrderRepository orderRepository;

    @Override
    public boolean evaluate(Long userId, TierCriteria criteria) {
        LocalDateTime since = LocalDateTime.now().minusDays(criteria.getEvaluationPeriodDays());
        BigDecimal total = orderRepository.sumAmountByUserIdAfter(userId, since);
        return total.compareTo(new BigDecimal(criteria.getThresholdValue())) >= 0;
    }

    @Override
    public CriteriaType getSupportedCriteriaType() {
        return CriteriaType.MIN_ORDER_VALUE;
    }
}
