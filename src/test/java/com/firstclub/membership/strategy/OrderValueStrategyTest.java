package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderValueStrategyTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderValueStrategy strategy;

    @Test
    void shouldReturnTrueWhenOrderValueMeetsThreshold() {
        TierCriteria criteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_VALUE)
                .thresholdValue("10000")
                .evaluationPeriodDays(30)
                .build();
        when(orderRepository.sumAmountByUserIdAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("12000"));
        assertThat(strategy.evaluate(1L, criteria)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenOrderValueBelowThreshold() {
        TierCriteria criteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_VALUE)
                .thresholdValue("10000")
                .evaluationPeriodDays(30)
                .build();
        when(orderRepository.sumAmountByUserIdAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("5000"));
        assertThat(strategy.evaluate(1L, criteria)).isFalse();
    }

    @Test
    void shouldReturnTrueWhenValueExactlyEqualsThreshold() {
        TierCriteria criteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_VALUE)
                .thresholdValue("10000")
                .evaluationPeriodDays(30)
                .build();
        when(orderRepository.sumAmountByUserIdAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("10000"));
        assertThat(strategy.evaluate(1L, criteria)).isTrue();
    }

    @Test
    void shouldReturnCorrectCriteriaType() {
        assertThat(strategy.getSupportedCriteriaType()).isEqualTo(CriteriaType.MIN_ORDER_VALUE);
    }
}
