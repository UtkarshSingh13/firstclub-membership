package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCountStrategyTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderCountStrategy strategy;

    @Test
    void shouldReturnTrueWhenOrderCountMeetsThreshold() {
        TierCriteria criteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_COUNT)
                .thresholdValue("5")
                .evaluationPeriodDays(30)
                .build();
        when(orderRepository.countByUserIdAndOrderDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(5L);
        assertThat(strategy.evaluate(1L, criteria)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenOrderCountBelowThreshold() {
        TierCriteria criteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_COUNT)
                .thresholdValue("5")
                .evaluationPeriodDays(30)
                .build();
        when(orderRepository.countByUserIdAndOrderDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(3L);
        assertThat(strategy.evaluate(1L, criteria)).isFalse();
    }

    @Test
    void shouldReturnCorrectCriteriaType() {
        assertThat(strategy.getSupportedCriteriaType()).isEqualTo(CriteriaType.MIN_ORDER_COUNT);
    }
}
