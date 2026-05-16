package com.firstclub.membership.entity;

import com.firstclub.membership.enums.BenefitType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tier_benefits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TierBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BenefitType benefitType;

    @Column(nullable = false)
    private String benefitValue;

    private String description;
}
