package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "membership_tiers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MembershipTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private Integer rank;

    private String description;
}
