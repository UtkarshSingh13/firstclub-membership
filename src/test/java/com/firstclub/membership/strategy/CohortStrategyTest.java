package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CohortStrategyTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CohortStrategy strategy;

    @Test
    void shouldReturnTrueWhenUserCohortMatches() {
        TierCriteria criteria = TierCriteria.builder()
                .criteriaType(CriteriaType.COHORT)
                .thresholdValue("EARLY_ADOPTER")
                .build();
        User user = User.builder().id(1L).cohort("EARLY_ADOPTER").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThat(strategy.evaluate(1L, criteria)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserCohortDoesNotMatch() {
        TierCriteria criteria = TierCriteria.builder()
                .criteriaType(CriteriaType.COHORT)
                .thresholdValue("EARLY_ADOPTER")
                .build();
        User user = User.builder().id(1L).cohort("REGULAR").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThat(strategy.evaluate(1L, criteria)).isFalse();
    }

    @Test
    void shouldReturnFalseWhenUserNotFound() {
        TierCriteria criteria = TierCriteria.builder()
                .criteriaType(CriteriaType.COHORT)
                .thresholdValue("EARLY_ADOPTER")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThat(strategy.evaluate(1L, criteria)).isFalse();
    }

    @Test
    void shouldReturnFalseWhenUserCohortIsNull() {
        TierCriteria criteria = TierCriteria.builder()
                .criteriaType(CriteriaType.COHORT)
                .thresholdValue("EARLY_ADOPTER")
                .build();
        User user = User.builder().id(1L).cohort(null).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThat(strategy.evaluate(1L, criteria)).isFalse();
    }

    @Test
    void shouldReturnCorrectCriteriaType() {
        assertThat(strategy.getSupportedCriteriaType()).isEqualTo(CriteriaType.COHORT);
    }
}
