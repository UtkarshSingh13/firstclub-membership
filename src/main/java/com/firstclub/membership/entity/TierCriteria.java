package com.firstclub.membership.entity;

import com.firstclub.membership.enums.CriteriaType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tier_criteria")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TierCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CriteriaType criteriaType;

    @Column(nullable = false)
    private String thresholdValue;

    private Integer evaluationPeriodDays;
}
