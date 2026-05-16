package com.firstclub.membership.repository;

import com.firstclub.membership.entity.TierCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TierCriteriaRepository extends JpaRepository<TierCriteria, Long> {
    List<TierCriteria> findByTierId(Long tierId);
}
