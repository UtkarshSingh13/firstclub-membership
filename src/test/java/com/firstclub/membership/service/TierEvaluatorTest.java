package com.firstclub.membership.service;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.TierCriteriaRepository;
import com.firstclub.membership.strategy.TierEvaluationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TierEvaluatorTest {

    @Mock private MembershipTierRepository tierRepository;
    @Mock private TierCriteriaRepository criteriaRepository;
    @Mock private TierEvaluationStrategy orderCountStrategy;
    @Mock private TierEvaluationStrategy orderValueStrategy;

    private TierEvaluator tierEvaluator;
    private MembershipTier silver, gold, platinum;

    @BeforeEach
    void setUp() {
        silver = MembershipTier.builder().id(1L).name("SILVER").rank(1).build();
        gold = MembershipTier.builder().id(2L).name("GOLD").rank(2).build();
        platinum = MembershipTier.builder().id(3L).name("PLATINUM").rank(3).build();

        when(orderCountStrategy.getSupportedCriteriaType()).thenReturn(CriteriaType.MIN_ORDER_COUNT);
        when(orderValueStrategy.getSupportedCriteriaType()).thenReturn(CriteriaType.MIN_ORDER_VALUE);

        tierEvaluator = new TierEvaluator(
                List.of(orderCountStrategy, orderValueStrategy),
                tierRepository, criteriaRepository);
    }

    @Test
    void shouldReturnPlatinumWhenPlatinumCriteriaMet() {
        when(tierRepository.findAllByOrderByRankDesc()).thenReturn(List.of(platinum, gold, silver));
        TierCriteria platinumCriteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_COUNT).thresholdValue("15").build();
        when(criteriaRepository.findByTierId(3L)).thenReturn(List.of(platinumCriteria));
        when(orderCountStrategy.evaluate(eq(1L), any())).thenReturn(true);

        MembershipTier result = tierEvaluator.evaluateHighestTier(1L);
        assertThat(result.getName()).isEqualTo("PLATINUM");
    }

    @Test
    void shouldReturnGoldWhenOnlyGoldCriteriaMet() {
        when(tierRepository.findAllByOrderByRankDesc()).thenReturn(List.of(platinum, gold, silver));
        TierCriteria platinumCriteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_COUNT).thresholdValue("15").build();
        when(criteriaRepository.findByTierId(3L)).thenReturn(List.of(platinumCriteria));
        when(orderCountStrategy.evaluate(eq(1L), eq(platinumCriteria))).thenReturn(false);

        TierCriteria goldCriteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_COUNT).thresholdValue("5").build();
        when(criteriaRepository.findByTierId(2L)).thenReturn(List.of(goldCriteria));
        when(orderCountStrategy.evaluate(eq(1L), eq(goldCriteria))).thenReturn(true);

        MembershipTier result = tierEvaluator.evaluateHighestTier(1L);
        assertThat(result.getName()).isEqualTo("GOLD");
    }

    @Test
    void shouldReturnSilverWhenNoCriteriaMet() {
        when(tierRepository.findAllByOrderByRankDesc()).thenReturn(List.of(platinum, gold, silver));
        TierCriteria platinumCriteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_COUNT).thresholdValue("15").build();
        when(criteriaRepository.findByTierId(3L)).thenReturn(List.of(platinumCriteria));
        when(orderCountStrategy.evaluate(eq(1L), any())).thenReturn(false);

        TierCriteria goldCriteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_COUNT).thresholdValue("5").build();
        when(criteriaRepository.findByTierId(2L)).thenReturn(List.of(goldCriteria));
        when(criteriaRepository.findByTierId(1L)).thenReturn(List.of());

        MembershipTier result = tierEvaluator.evaluateHighestTier(1L);
        assertThat(result.getName()).isEqualTo("SILVER");
    }

    @Test
    void shouldQualifyWithOrLogic_anyOneCriteriaSuffices() {
        when(tierRepository.findAllByOrderByRankDesc()).thenReturn(List.of(platinum, gold, silver));
        when(criteriaRepository.findByTierId(3L)).thenReturn(List.of());

        TierCriteria goldCountCriteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_COUNT).thresholdValue("5").build();
        TierCriteria goldValueCriteria = TierCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_VALUE).thresholdValue("10000").build();
        when(criteriaRepository.findByTierId(2L)).thenReturn(List.of(goldCountCriteria, goldValueCriteria));

        when(orderCountStrategy.evaluate(eq(1L), eq(goldCountCriteria))).thenReturn(false);
        when(orderValueStrategy.evaluate(eq(1L), eq(goldValueCriteria))).thenReturn(true);

        MembershipTier result = tierEvaluator.evaluateHighestTier(1L);
        assertThat(result.getName()).isEqualTo("GOLD");
    }
}
