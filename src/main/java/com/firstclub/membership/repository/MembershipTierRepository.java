package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
    Optional<MembershipTier> findByName(String name);
    List<MembershipTier> findAllByOrderByRankDesc();
    List<MembershipTier> findAllByOrderByRankAsc();
}
