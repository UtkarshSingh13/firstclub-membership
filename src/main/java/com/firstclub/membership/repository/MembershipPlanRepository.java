package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.enums.PlanName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> findByActiveTrue();
    Optional<MembershipPlan> findByName(PlanName name);
}
