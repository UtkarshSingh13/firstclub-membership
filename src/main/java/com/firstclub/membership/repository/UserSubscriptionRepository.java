package com.firstclub.membership.repository;

import com.firstclub.membership.entity.UserSubscription;
import com.firstclub.membership.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);
    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);
}
