package com.firstclub.membership.entity;

import com.firstclub.membership.enums.PlanName;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "membership_plans")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PlanName name;

    @Column(nullable = false)
    private Integer durationInDays;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean active;
}
